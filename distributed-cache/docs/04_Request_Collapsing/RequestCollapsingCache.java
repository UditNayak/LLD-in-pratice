import java.util.concurrent.*;

public class RequestCollapsingCache<K, V> {
    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<K, CompletableFuture<V>> pendingRequests = new ConcurrentHashMap<>();

    // Simulated async DB fetch (replace with actual DB Logic)
    private CompletableFuture<V> asyncDbFetch(K key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + " - Fetching from DB for key: " + key);
                Thread.sleep(1000);  // Simulate DB latency
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return (V) ("DBValue-for-" + key);
        });
    }

    // Put method for caching values
    public void put(K key, V value) {
        cache.put(key, value);
        System.out.println(Thread.currentThread().getName() + " - Put key=" + key + ", value=" + value);
    }

    // Get method with request collapsing logic
    public CompletableFuture<V> get(K key) {
        // 1. Check cache
        V cachedValue = cache.get(key);
        if (cachedValue != null) {
            return CompletableFuture.completedFuture(cachedValue);
        }

        // 2. Check if a request is already pending for this key
        CompletableFuture<V> future = pendingRequests.get(key);
        if (future != null) {
            // There's already a fetch ongoing, return its future
            return future;
        }

        // 3. No pending request, create a new one
        CompletableFuture<V> newFuture = new CompletableFuture<>();

        // 4. Try to put this future in the map; if someone else won the race, use theirs
        CompletableFuture<V> existingFuture = pendingRequests.putIfAbsent(key, newFuture);
        if (existingFuture == null) {
            // We are the first to request this key, initiate the DB fetch
            asyncDbFetch(key).thenAccept(value -> {
                System.out.println(Thread.currentThread().getName() + " - Completed DB fetch for key: " + key);
                cache.put(key, value);               // Cache the value
                newFuture.complete(value);           // Complete our future
                pendingRequests.remove(key);         // Remove from pending requests
            }).exceptionally(ex -> {
                newFuture.completeExceptionally(ex);
                pendingRequests.remove(key);
                return null;
            });
            return newFuture;
        } else {
            // Someone else is already fetching, return their future
            return existingFuture;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        RequestCollapsingCache<String, String> cache = new RequestCollapsingCache<>();

        // Create a task that calls get() method
        Runnable getTask = () -> {
            cache.get("user123").thenAccept(value -> {
                System.out.println(Thread.currentThread().getName() + " - Got value: " + value);
            }).exceptionally(ex -> {
                System.out.println(Thread.currentThread().getName() + " - Error: " + ex.getMessage());
                return null;
            });
        };

        // Start multiple threads to simulate concurrent requests
        Thread t1 = new Thread(getTask, "Thread-1");
        Thread t2 = new Thread(getTask, "Thread-2");
        Thread t3 = new Thread(getTask, "Thread-3");

        t1.start();
        t2.start();
        t3.start();

        // Wait for all threads to finish
        t1.join();
        t2.join();
        t3.join();

        // Only after all fetches complete, insert a value manually (if needed)
        cache.put("user123", "Manually-Inserted-Value");

        // After DB fetch completes, retrieve it again
        cache.get("user123").thenAccept(value -> {
            System.out.println("After put, got value: " + value);
        }).join();
    }
}
