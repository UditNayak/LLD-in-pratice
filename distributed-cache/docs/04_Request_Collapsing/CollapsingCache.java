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

public class CollapsingCache<K, V> {
    private final Map<K, V> cacheData = new ConcurrentHashMap<>();
    private final Database<K, V> database;

    // Tracks in-progress fetches
    private final ConcurrentHashMap<K, CompletableFuture<V>> pendingRequests = new ConcurrentHashMap<>();

    public CollapsingCache(Database<K, V> db) {
        this.database = db;
    }

    public V get(K key) {
        // 1. Check cache first
        if (cacheData.containsKey(key)) {
            return cacheData.get(key);
        }

        // 2. Check if a request is already pending for this key
        CompletableFuture<V> future = pendingRequests.get(key);
        if (future != null) {
            // There's already a fetch ongoing, wait for it to complete
            return future.join();
        }

        // 3. No pending request, create a new one
        CompletableFuture<V> newFuture = new CompletableFuture<>();

        // 4. Try to put this future in the map; if someone else won the race, then use theirs
        CompletableFuture<V> existingFuture = pendingRequests.putIfAbsent(key, newFuture);
        if (existingFuture == null) {
            // We are the first to request this key, initiate the DB fetch
            CompletableFuture.supplyAsync(() -> database.fetch(key))
                .thenAccept(value -> {
                    cacheData.put(key, value);       // Cache the value
                    newFuture.complete(value);       // Complete our future
                    pendingRequests.remove(key);     // Remove from pending requests
                })
                .exceptionally(ex -> {
                    newFuture.completeExceptionally(ex);
                    pendingRequests.remove(key);
                    return null;
                });
            System.out.println(Thread.currentThread().getName() + " - Initiated DB fetch for key: " + key);
            return newFuture.join();
        } else {
            // Someone else is already fetching, wait for their future
            System.out.println(Thread.currentThread().getName() + " - Waiting for existing fetch for key: " + key);
            return existingFuture.join();
        }
    }

    public void put(K key, V value) {
        cacheData.put(key, value);
        database.save(key, value);
    }

    public static void main(String[] args) {
        Database<String, String> db = new Database<>();
        db.save("user:1", "Alice");
        CollapsingCache<String, String> cache = new CollapsingCache<>(db);

        // Simulate 10 parallel requests for the same key
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                String value = cache.get("user:1");
                System.out.println(Thread.currentThread().getName() + " - Fetched value: " + value);
            });
        }
        executor.shutdown();
    }

}

/*
Expected Output (order may vary due to concurrency):
pool-1-thread-1 - Initiated DB fetch for key: user:1
pool-1-thread-2 - Waiting for existing fetch for key: user:1
pool-1-thread-3 - Waiting for existing fetch for key: user:1
pool-1-thread-4 - Waiting for existing fetch for key: user:1
pool-1-thread-5 - Waiting for existing fetch for key: user:1
pool-1-thread-6 - Waiting for existing fetch for key: user:1
pool-1-thread-7 - Waiting for existing fetch for key: user:1
pool-1-thread-8 - Waiting for existing fetch for key: user:1
pool-1-thread-9 - Waiting for existing fetch for key: user:1
pool-1-thread-10 - Waiting for existing fetch for key: user:1
ForkJoinPool.commonPool-worker-1 - Fetched value Successfully from DB for key: user:1
pool-1-thread-1 - Fetched value: Alice
pool-1-thread-2 - Fetched value: Alice
pool-1-thread-3 - Fetched value: Alice
pool-1-thread-4 - Fetched value: Alice
pool-1-thread-5 - Fetched value: Alice
pool-1-thread-6 - Fetched value: Alice
pool-1-thread-7 - Fetched value: Alice
pool-1-thread-8 - Fetched value: Alice
pool-1-thread-9 - Fetched value: Alice
pool-1-thread-10 - Fetched value: Alice
 */


/*
 How it works:
    1. 0 threads start at the same time requesting the same key "user:1".
    2. Thread 1 wins the race
        - It checks the cache, misses.
        - Checks pendingRequests, misses.
        - Creates a new CompletableFuture and puts it in pendingRequests.
        - It kicks off an async DB fetch via CompletableFuture.supplyAsync(...).
        - This DB fetch doesn’t run on the same thread, but on the ForkJoinPool.commonPool (so we’ll see a worker thread like ForkJoinPool.commonPool-worker-1).
        - Thread 1 then calls newFuture.join(), blocking until DB returns.
        - Thread 1 is acting like a leader/coordinator for this request.
    3. All other threads (2 to 10) come in while Thread 1 is waiting for DB.
        - They check the cache, miss.
        - They check pendingRequests, hit (because Thread 1 put the future there).
        - They call existingFuture.join(), blocking until Thread 1’s future completes.
        - They are followers, waiting for the leader to complete the fetch.
    3. DB fetch completes on Thread 11 (ForkJoinPool.commonPool-worker-1)
        - It puts the value in cache.
        - It completes Thread 1’s future.
        - It removes the entry from pendingRequests.
    4. Thread 1 unblocks from join(), gets the value, and prints it
    5. All other threads (2 to 10) unblock from join(), get the same value, and print it
    
 */