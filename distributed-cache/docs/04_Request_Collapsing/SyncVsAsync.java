import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Database<K, V> {
    private final Map<K, V> dbStore = new ConcurrentHashMap<>();

    public void save(K key, V value) {
        dbStore.put(key, value);
    }

    public V fetch(K key) {
        try {
            Thread.sleep(3000); // Simulate DB latency
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println(Thread.currentThread().getName() + " - Fetched value Successfully from DB for key: " + key);
        return dbStore.getOrDefault(key, null);
    }
}

// ------------------ ASYNC version ------------------
class CollapsingCacheAsync<K, V> {
    private final Map<K, CompletableFuture<V>> cache = new ConcurrentHashMap<>();
    private final Database<K, V> database;
    private final ConcurrentHashMap<K, CompletableFuture<V>> pendingRequests = new ConcurrentHashMap<>();

    public CollapsingCacheAsync(Database<K, V> db) {
        this.database = db;
    }

    // Return a Future instead of blocking
    public CompletableFuture<V> getAsync(K key) {
        // Check cache
        CompletableFuture<V> cached = cache.get(key);
        if (cached != null) {
            return cached;
        }

        // Check pending requests
        CompletableFuture<V> pending = pendingRequests.get(key);
        if (pending != null) {
            return pending;
        }

        // Start new DB fetch
        CompletableFuture<V> newFuture = new CompletableFuture<>();
        if (pendingRequests.putIfAbsent(key, newFuture) == null) {
            CompletableFuture.supplyAsync(() -> database.fetch(key))
                .thenAccept(value -> {
                    cache.put(key, CompletableFuture.completedFuture(value));
                    newFuture.complete(value);
                    pendingRequests.remove(key);
                })
                .exceptionally(ex -> {
                    newFuture.completeExceptionally(ex);
                    pendingRequests.remove(key);
                    return null;
                });
            System.out.println(Thread.currentThread().getName() + " - Initiated DB fetch for key: " + key);
        }
        return newFuture;
    }
}

// ------------------ SYNC wrapper ------------------
class CollapsingCacheSync<K, V> {
    private final CollapsingCacheAsync<K, V> asyncCache;

    public CollapsingCacheSync(CollapsingCacheAsync<K, V> asyncCache) {
        this.asyncCache = asyncCache;
    }

    // Block until value is available
    public V get(K key) {
        return asyncCache.getAsync(key).join();
    }
}

// ------------------ DEMO ------------------
public class SyncVsAsync {
    public static void main(String[] args) {
        Database<String, String> db = new Database<>();
        for (int i = 1; i <= 3; i++) {
            db.save("user:" + i, "User-" + i);
        }

        CollapsingCacheAsync<String, String> asyncCache = new CollapsingCacheAsync<>(db);
        CollapsingCacheSync<String, String> syncCache = new CollapsingCacheSync<>(asyncCache);

        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Async usage (non-blocking)
        System.out.println("\n=== Async Example ===");
        for (int i = 1; i <= 3; i++) {
            final String key = "user:" + i;
            asyncCache.getAsync(key)
                .thenAccept(value ->
                    System.out.println(Thread.currentThread().getName() + " - Async fetched: " + value));
        }

        // Sync usage (blocking)
        System.out.println("\n=== Sync Example ===");
        for (int i = 1; i <= 3; i++) {
            final String key = "user:" + i;
            executor.submit(() -> {
                String value = syncCache.get(key);
                System.out.println(Thread.currentThread().getName() + " - Sync fetched: " + value);
            });
        }

        executor.shutdown();
    }
}

/*
 Example Output: (actual output may vary due to threading)
=== Async Example ===
main - Initiated DB fetch for key: user:1
main - Initiated DB fetch for key: user:2
main - Initiated DB fetch for key: user:3

=== Sync Example ===
ForkJoinPool.commonPool-worker-1 - Fetched value Successfully from DB for key: user:1
ForkJoinPool.commonPool-worker-1 - Async fetched: User-1
pool-1-thread-1 - Sync fetched: User-1
ForkJoinPool.commonPool-worker-2 - Fetched value Successfully from DB for key: user:2
ForkJoinPool.commonPool-worker-2 - Async fetched: User-2
pool-1-thread-2 - Sync fetched: User-2
ForkJoinPool.commonPool-worker-3 - Fetched value Successfully from DB for key: user:3
ForkJoinPool.commonPool-worker-3 - Async fetched: User-3
pool-1-thread-3 - Sync fetched: User-3
 */