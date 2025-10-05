import java.util.HashMap;
import java.util.Map;

public class LocalityPrefetchingCache {
    private final Map<String, String> cache = new HashMap<>();
    
    // Simulate DB fetch (replace with actual DB Logic)
    private String dbFetch(String key) {
        System.out.println("Fetching from DB for key: " + key);
        // Simulate DB delay
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Return a dummy value
        return "DBValue-for-" + key;
    }

    // Simple Prefetching Mechanism: Prefetch adjacent keys (for spatial locality)
    private void prefetchAdjacentKeys(String key) {
        // Convert key to simulate a numeric-based cache (e.g., "key1", "key2", "key3", ...)
        int keyIndex = Integer.parseInt(key.substring(3)); // Assuming key is of form "key1", "key2", ...
        
        // Construct next and previous key
        String nextKey = "key" + (keyIndex + 1);
        String prevKey = "key" + (keyIndex - 1);
        
        // Prefetch the adjacent keys (just simulate fetching)
        get(prevKey);  // Prefetch previous key
        get(nextKey);  // Prefetch next key
    }

    // Put method to insert key-value pairs into cache
    public void put(String key, String value) {
        cache.put(key, value);
        System.out.println("Put key=" + key + ", value=" + value);
    }

    // Get method to retrieve data from cache with prefetching mechanism
    public String get(String key) {
        // 1. Check if the key exists in cache
        if (cache.containsKey(key)) {
            return cache.get(key);  // Return cached value
        }

        // 2. If key not found, fetch from DB (simulate DB fetch)
        String valueFromDb = dbFetch(key);

        // 3. Cache the value after fetching it from DB
        cache.put(key, valueFromDb);

        // 4. Start prefetching adjacent keys for locality
        prefetchAdjacentKeys(key);

        return valueFromDb;  // Return the value fetched from DB
    }

    public static void main(String[] args) {
        LocalityPrefetchingCache cache = new LocalityPrefetchingCache();

        // Simulate fetching data for key1
        System.out.println("First Accessing key1: " + cache.get("key1"));
        
        // Manually put a new value and get it
        cache.put("key1", "Manually-Inserted-Value");
        
        System.out.println("After Put, Accessing key1: " + cache.get("key1"));
        
        // Fetching other keys for illustration
        System.out.println("Accessing key3: " + cache.get("key3"));
    }
}
