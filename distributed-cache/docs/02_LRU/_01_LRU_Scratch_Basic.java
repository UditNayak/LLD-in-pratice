import java.util.HashMap;
import java.util.Map;

/**
 * Basic LRU Cache implementation using a HashMap and a custom Doubly Linked List.
 * Uses integer keys and values, avoiding Generics for simplicity.
 */
public class _01_LRU_Scratch_Basic {
    // Node class for Doubly Linked List
    class Node {
        int key, value;
        Node prev, next;

        Node(int key, int value){
            this.key = key;
            this.value = value;
        }
    }

    private Map<Integer, Node> mp;
    private Node head, tail;            // Dummy head and tail nodes for DLL
    private int capacity;               // Maximum capacity of the cache

    public _01_LRU_Scratch_Basic(int capacity) {
        this.capacity = capacity;
        this.mp = new HashMap<>();

        // Dummy head and tail for easier operations
        this.head = new Node(-1, -1);
        this.tail = new Node(-1, -1);

        // head <-> tail
        head.next = tail;
        tail.prev = head;
    }
    
    public int get(int key) {
        if (!mp.containsKey(key)) {
            return -1;  // not found
        }

        Node node = mp.get(key);

        // MRU [Most Recently Used] --> head
        remove(node);
        insertAtHead(node);

        return node.value;
    }
    
    public void put(int key, int value) {
        if (mp.containsKey(key)){
            // Key exists: update existing value and move node[MRU] to head
            Node node = mp.get(key);
            node.value = value;
            remove(node);
            insertAtHead(node);
        } else {
            // If cache is full, evict LRU [Least Recently Used]
            if (mp.size() == capacity){
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

    // Insert node right after head (MRU)
    // Before: head <-> nextNode
    // After: head <-> node <-> nextNode
    private void insertAtHead(Node node) {
        node.next = head.next;
        node.prev = head;

        head.next.prev = node;
        head.next = node;
    }

    // For debugging: print the current state of the cache
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node curr = head.next;
        while (curr != tail) {
            sb.append("[").append(curr.key).append(":").append(curr.value).append("] ");
            curr = curr.next;
        }
        return sb.toString().trim();
    }

    public static void main(String[] args) {
        _01_LRU_Scratch_Basic lruCache = new _01_LRU_Scratch_Basic(3);

        lruCache.put(1, 10);
        lruCache.put(2, 20);
        lruCache.put(3, 30);
        System.out.println(lruCache); // Expected: [3:30] [2:20] [1:10]

        lruCache.get(2);
        System.out.println(lruCache); // Expected: [2:20] [3:30] [1:10]

        lruCache.put(4, 40); // Evicts key 1
        System.out.println(lruCache); // Expected: [4:40] [2:20] [3:30]

        System.out.println(lruCache.get(1)); // Expected: -1 (not found)
        System.out.println(lruCache.get(3)); // Expected: 30
        System.out.println(lruCache); // Expected: [3:30] [4:40] [2:20]
    }
}
