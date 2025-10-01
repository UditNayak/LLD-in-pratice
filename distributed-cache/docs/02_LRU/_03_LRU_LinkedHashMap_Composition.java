import java.util.LinkedHashMap;
import java.util.Map;

/*
 * In real-world, you usually don’t need to reinvent LRU — the JDK already provides support for it via LinkedHashMap.
 * LinkedHashMap maintains insertion order or access order (if configured).
 * If you enable access order, then whenever you call get() or put(), the accessed entry is moved to the end of the map, making it easy to track least recently used.
 */
public class _03_LRU_LinkedHashMap_Composition<K, V> {
    private final int capacity;
    private final LinkedHashMap<K, V> map;

    public _03_LRU_LinkedHashMap_Composition(int capacity) {
        this.capacity = capacity;
        
        // LinkedHashMap constructor:
        // - capacity: Sets the maximum capacity of the map
        // - 0.75f: Load factor (threshold for resizing, not essential for LRU but standard in HashMap)
        // - true: Access order mode (important for LRU behavior)
        this.map = new LinkedHashMap<>(capacity, 0.75f, true) {
            // Overriding removeEldestEntry to automatically evict LRU entry
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                // If the cache size exceeds the capacity, evict the eldest (least recently used) entry
                return size() > _03_LRU_LinkedHashMap_Composition.this.capacity;
            }
        };
    }

    public V get(K key) {
        return map.getOrDefault(key, null);
    }

    public void put(K key, V value) {
        map.put(key, value);
    }

    // For testing: Print current state of cache in a readable format
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            sb.append("[").append(entry.getKey()).append(":").append(entry.getValue()).append("] ");
        }
        return sb.toString().trim();
    }

    public static void main(String[] args) {
        _03_LRU_LinkedHashMap_Composition<Integer, String> lruCache = new _03_LRU_LinkedHashMap_Composition<>(3);

        lruCache.put(1, "One");
        lruCache.put(2, "Two");
        lruCache.put(3, "Three");
        System.out.println(lruCache);  // Expected: [1:One] [2:Two] [3:Three]

        lruCache.get(2);
        System.out.println(lruCache);  // Expected: [1:One] [3:Three] [2:Two]

        lruCache.put(4, "Four"); // Evicts key 1
        System.out.println(lruCache);  // Expected: [3:Three] [2:Two] [4:Four]

        System.out.println(lruCache.get(1)); // Expected: null (not found)
        System.out.println(lruCache.get(3)); // Expected: Three
        System.out.println(lruCache);  // Expected: [2:Two] [4:Four] [3:Three]
    }
    
}
