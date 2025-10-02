import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

public class Consistent_Hashing_Basic {
    private final TreeMap<Long, String> ring;
    private final int numberOfReplicas;
    private final MessageDigest md;
    
    public Consistent_Hashing_Basic(int numberOfReplicas) throws NoSuchAlgorithmException {
        this.ring = new TreeMap<>();
        this.numberOfReplicas = numberOfReplicas;
        this.md = MessageDigest.getInstance("MD5");
    }

    // Add node with virtual replicas
    public void addServer(String server) {
        for (int i=0; i<numberOfReplicas; i++) {
            long hash = generateHash(server + i);
            ring.put(hash, server);
        }
    }

    public void removeServer(String server) {
        for (int i=0; i<numberOfReplicas; i++) {
            long hash = generateHash(server + i);
            ring.remove(hash);
        }
    }

    public String getServer(String key) {
        if (ring.isEmpty()) return null;

        long hash = generateHash(key);
         // Find the smallest key >= hash
        Long target = ring.ceilingKey(hash);

        // If no such key, wrap around to the first key in the ring
        if (target == null) {
            target = ring.firstKey();
        }

        return ring.get(target);
    }

    private long generateHash(String key){
        md.reset();
        byte[] keyBytes = key.getBytes();

        md.update(keyBytes);
        byte[] digest = md.digest();

        // Take first 4 bytes and convert to int
        // Use long to avoid negative values
        long hash = ((long)(digest[0] & 0xFF) << 24) |
                    ((long)(digest[1] & 0xFF) << 16) |
                    ((long)(digest[2] & 0xFF) << 8) |
                    ((long)(digest[3] & 0xFF));
        return hash;
    }

    @Override
    public String toString(){
        return ring.toString();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Consistent_Hashing_Basic ch = new Consistent_Hashing_Basic(3);

        // Add servers
        ch.addServer("server1");
        ch.addServer("server2");
        ch.addServer("server3");

        System.out.println("Initial ring:");
        System.out.println(ch);

        // Test keys before removal
        String[] keys = {"key1", "key2", "key3", "key4", "key5", "key6"};

        System.out.println("\nKey mappings before removing any server:");
        for (String key : keys) {
            System.out.println(key + " is mapped to " + ch.getServer(key));
        }

        // Remove one server
        ch.removeServer("server1");
        System.out.println("\nRing after removing server1:");
        System.out.println(ch);

        // Test keys after removal
        System.out.println("\nKey mappings after removing server1:");
        for (String key : keys) {
            System.out.println(key + " is mapped to " + ch.getServer(key));
        }
    }
}

/*
Output:

Initial ring:
{853932351=server2, 1229543767=server2, 2431002893=server3, 3333114503=server3, 3395364710=server2, 3398945014=server3, 3580395949=server1, 3726572320=server1, 4030069192=server1}

Key mappings before removing any server:
key1 is mapped to server3
key2 is mapped to server3
key3 is mapped to server2
key4 is mapped to server1
key5 is mapped to server2
key6 is mapped to server3

Ring after removing server1:
{853932351=server2, 1229543767=server2, 2431002893=server3, 3333114503=server3, 3395364710=server2, 3398945014=server3}

Key mappings after removing server1:
key1 is mapped to server3
key2 is mapped to server3
key3 is mapped to server2
key4 is mapped to server2
key5 is mapped to server2
key6 is mapped to server3
*/
