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

public class CollapsingCacheDifferentKeys<K, V> {
    private final Map<K, V> cacheData = new ConcurrentHashMap<>();
    private final Database<K, V> database;
    
    // Tracks in-progress fetches
    private final ConcurrentHashMap<K, CompletableFuture<V>> pendingRequests = new ConcurrentHashMap<>();

    public CollapsingCacheDifferentKeys(Database<K, V> db) {
        this.database = db;
    }

    public V get(K key) {
        // Check cache first
        if (cacheData.containsKey(key)) {
            return cacheData.get(key);
        }

        // Atomically compute or retrieve existing pending fetch
        CompletableFuture<V> future = pendingRequests.computeIfAbsent(key, k -> {
            // Initiate the DB fetch asynchronously
            CompletableFuture<V> newFuture = CompletableFuture.supplyAsync(() -> database.fetch(k));

            // .whenComplete handles both success and failure cases in the same block
            newFuture.whenComplete((value, ex) -> {
                if (ex == null) {
                    cacheData.put(k, value);
                } else {
                    System.err.println("Error fetching key: " + k + " - " + ex);
                }
                pendingRequests.remove(k);
            });
            System.out.println(Thread.currentThread().getName() + " - Initiated DB fetch for key: " + k);
            return newFuture;
        });

        // Wait for the result
        return future.join();
    }

    public void put(K key, V value) {
        cacheData.put(key, value);
        database.save(key, value);
    }

    public static void main(String[] args) {
        Database<String, String> db = new Database<>();
        for (int i = 1; i <= 10; i++) {
            db.save("user:" + i, "User-" + i);
        }

        CollapsingCacheDifferentKeys<String, String> cache = new CollapsingCacheDifferentKeys<>(db);
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Simulate 10 parallel requests for 10 different keys
        for (int i = 1; i <= 10; i++) {
            final String key = "user:" + i;
            executor.submit(() -> {
                String value = cache.get(key);
                System.out.println(Thread.currentThread().getName() + " - Fetched value: " + value);
            });
        }
        executor.shutdown();
    }

}

/*
 Example Output: (actual output may vary due to thread scheduling)
pool-1-thread-1 - Initiated DB fetch for key: user:1
pool-1-thread-2 - Initiated DB fetch for key: user:2
pool-1-thread-3 - Initiated DB fetch for key: user:3
pool-1-thread-4 - Initiated DB fetch for key: user:4
pool-1-thread-5 - Initiated DB fetch for key: user:5
pool-1-thread-6 - Initiated DB fetch for key: user:6
pool-1-thread-7 - Initiated DB fetch for key: user:7
pool-1-thread-8 - Initiated DB fetch for key: user:8
pool-1-thread-9 - Initiated DB fetch for key: user:9
pool-1-thread-10 - Initiated DB fetch for key: user:10
ForkJoinPool.commonPool-worker-1 - Fetched value Successfully from DB for key: user:1
pool-1-thread-1 - Fetched value: User-1
ForkJoinPool.commonPool-worker-2 - Fetched value Successfully from DB for key: user:2
pool-1-thread-2 - Fetched value: User-2
ForkJoinPool.commonPool-worker-3 - Fetched value Successfully from DB for key: user:3
pool-1-thread-3 - Fetched value: User-3
ForkJoinPool.commonPool-worker-4 - Fetched value Successfully from DB for key: user:4
pool-1-thread-4 - Fetched value: User-4
ForkJoinPool.commonPool-worker-5 - Fetched value Successfully from DB for key: user:5
pool-1-thread-5 - Fetched value: User-5
ForkJoinPool.commonPool-worker-6 - Fetched value Successfully from DB for key: user:6
pool-1-thread-6 - Fetched value: User-6
ForkJoinPool.commonPool-worker-7 - Fetched value Successfully from DB for key: user:7
pool-1-thread-7 - Fetched value: User-7
ForkJoinPool.commonPool-worker-8 - Fetched value Successfully from DB for key: user:8
pool-1-thread-8 - Fetched value: User-8
ForkJoinPool.commonPool-worker-9 - Fetched value Successfully from DB for key: user:9
pool-1-thread-9 - Fetched value: User-9
ForkJoinPool.commonPool-worker-10 - Fetched value Successfully from DB for key: user:10
pool-1-thread-10 - Fetched value: User-10
 */