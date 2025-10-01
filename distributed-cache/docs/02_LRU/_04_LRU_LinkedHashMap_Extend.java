import java.util.LinkedHashMap;
import java.util.Map;

public class _04_LRU_LinkedHashMap_Extend<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public _04_LRU_LinkedHashMap_Extend(int capacity) {
        // accessOrder = true → orders entries by access (get/put)
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        // Remove the eldest entry if size exceeds capacity (LRU eviction)
        return size() > capacity;
    }

    public static void main(String[] args) {
        _04_LRU_LinkedHashMap_Extend<Integer, String> cache = new _04_LRU_LinkedHashMap_Extend<>(3);

        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");
        System.out.println(cache); // {1=One, 2=Two, 3=Three}

        cache.get(2);              // Access 2 → makes it most recently used (MRU)
        System.out.println(cache); // {1=One, 3=Three, 2=Two}

        cache.put(4, "Four");      // Evicts 1 (LRU)
        System.out.println(cache); // {3=Three, 2=Two, 4=Four}

        System.out.println(cache.get(1)); // null (1 not found)
        System.out.println(cache.get(3)); // Three
        System.out.println(cache); // {2=Two, 4=Four, 3=Three}
    }
}