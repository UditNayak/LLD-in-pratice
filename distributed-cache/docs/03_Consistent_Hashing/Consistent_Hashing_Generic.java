import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

public class Consistent_Hashing_Generic<T> {
    private final TreeMap<Long, T> ring;
    private final int numberOfReplicas;
    private final MessageDigest md;
    
    public Consistent_Hashing_Generic(int numberOfReplicas) throws NoSuchAlgorithmException {
        this.ring = new TreeMap<>();
        this.numberOfReplicas = numberOfReplicas;
        this.md = MessageDigest.getInstance("MD5");
    }

    // Add node with virtual replicas
    public void addServer(T server) {
        for (int i = 0; i < numberOfReplicas; i++) {
            long hash = generateHash(server.toString() + i);
            ring.put(hash, server);
        }
    }

    public void removeServer(T server) {
        for (int i = 0; i < numberOfReplicas; i++) {
            long hash = generateHash(server.toString() + i);
            ring.remove(hash);
        }
    }

    public T getServer(String key) {
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

    private long generateHash(String key) {
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

    // Debug: print ring structure
    @Override
    public String toString() {
        return ring.toString();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        // Test with custom Server class
        Consistent_Hashing_Generic<Server> ch = new Consistent_Hashing_Generic<>(3);

        Server s1 = new Server("192.168.0.1", 8080);
        Server s2 = new Server("192.168.0.2", 8080);
        Server s3 = new Server("192.168.0.3", 8080);

        ch.addServer(s1);
        ch.addServer(s2);
        ch.addServer(s3);

        System.out.println("Initial ring:");
        System.out.println(ch);

        String[] keys = {"key1", "key2", "key3", "key4", "key5", "key6"};

        System.out.println("\nKey mappings before removing any server:");
        for (String key : keys) {
            System.out.println(key + " is mapped to " + ch.getServer(key));
        }

        ch.removeServer(s1);
        System.out.println("\nRing after removing " + s1 + ":");
        System.out.println(ch);

        System.out.println("\nKey mappings after removing " + s1 + ":");
        for (String key : keys) {
            System.out.println(key + " is mapped to " + ch.getServer(key));
        }
    }

}

class Server {
    private final String ip;
    private final int port;

    public Server(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }
}

/*
Output:

Initial ring:
{54536046=192.168.0.1:8080, 203659980=192.168.0.3:8080, 240405924=192.168.0.2:8080, 718869031=192.168.0.2:8080, 960535761=192.168.0.3:8080, 1020991488=192.168.0.1:8080, 1709067614=192.168.0.1:8080, 3605626313=192.168.0.3:8080, 4066675151=192.168.0.2:8080}

Key mappings before removing any server:
key1 is mapped to 192.168.0.3:8080
key2 is mapped to 192.168.0.3:8080
key3 is mapped to 192.168.0.3:8080
key4 is mapped to 192.168.0.3:8080
key5 is mapped to 192.168.0.1:8080
key6 is mapped to 192.168.0.1:8080

Ring after removing 192.168.0.1:8080:
{203659980=192.168.0.3:8080, 240405924=192.168.0.2:8080, 718869031=192.168.0.2:8080, 960535761=192.168.0.3:8080, 3605626313=192.168.0.3:8080, 4066675151=192.168.0.2:8080}

Key mappings after removing 192.168.0.1:8080:
key1 is mapped to 192.168.0.3:8080
key2 is mapped to 192.168.0.3:8080
key3 is mapped to 192.168.0.3:8080
key4 is mapped to 192.168.0.3:8080
key5 is mapped to 192.168.0.3:8080
key6 is mapped to 192.168.0.3:8080
 */
