import java.util.HashMap;
import java.util.Map;

public class _02_LRU_Scratch_Generic<K, V> {
    // Node class for Doubly Linked List
    class Node {
        K key;
        V value;
        Node prev, next;

        Node(K key, V value){
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<K, Node> mp;
    private final Node head, tail;      // Dummy head and tail

    public _02_LRU_Scratch_Generic(int capacity) {
        this.capacity = capacity;
        this.mp = new HashMap<>();

        // Dummy head and tail for easier operation
        this.head = new Node(null, null);
        this.tail = new Node(null, null);

        // head <-> tail
        head.next = tail;
        tail.prev = head;
    }

    public V get(K key) {
        if (!mp.containsKey(key)) {
            return null;  // Not found
        }

        // MRU [Most Recently Used] --> head
        Node node = mp.get(key);
        remove(node);
        insertAtHead(node);
        return node.value;
    }

    // Put key-value pair
    public void put(K key, V value) {
        if (mp.containsKey(key)) {
            // Key exists: update existing value and move node[MRU] to head
            Node node = mp.get(key);
            node.value = value;
            remove(node);
            insertAtHead(node);
        } else {
            // If cache is full, evict LRU
            if (mp.size() == capacity) {
                Node lru = tail.prev;
                remove(lru);
                mp.remove(lru.key);
            }
            // MRU [Most Recently Used] --> head
            Node newNode = new Node(key, value);
            mp.put(key, newNode);
            insertAtHead(newNode);
        }
    }

    // Remove node from DLL
    // Before: prevNode <-> node <-> nextNode
    // After: prevNode <-> nextNode
    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    // Insert node right after HEAD (MRU)
    // Before: head <-> nextNode
    // After: head <-> node <-> nextNode
    private void insertAtHead(Node node) {
        node.next = head.next;
        node.prev = head;

        head.next.prev = node;
        head.next = node;
    }

    // For testing: Print current state of cache
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node curr = head.next; curr != tail; curr = curr.next) {
            sb.append("[" + curr.key + ":" + curr.value + "] ");
        }
        return sb.toString().trim();
    }

    public static void main(String[] args) {
        _02_LRU_Scratch_Generic<Integer, String> lruCache = new _02_LRU_Scratch_Generic<>(3);

        lruCache.put(1, "One");
        lruCache.put(2, "Two");
        lruCache.put(3, "Three");
        System.out.println(lruCache);  // Expected: [3:Three] [2:Two] [1:One]

        lruCache.get(2);
        System.out.println(lruCache);  // Expected: [2:Two] [3:Three] [1:One]

        lruCache.put(4, "Four"); // Evicts key 1
        System.out.println(lruCache);  // Expected: [4:Four] [2:Two] [3:Three]

        System.out.println(lruCache.get(1)); // Expected: null (not found)
        System.out.println(lruCache.get(3)); // Expected: Three
        System.out.println(lruCache);  // Expected: [3:Three] [4:Four] [2:Two]
    }
}
