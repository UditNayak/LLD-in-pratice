# Design a Distributed Key-Value Cache [Google Interview Question]

# V0: Simple Key-Value Cache
- Basic operations: get(key), put(key, value)
- The cache will talk to database and there can be different types of databases (SQL, NoSQL, etc.)

#### Cache
```
class Cache<K, V> {
    - cacheData: Map<K, V>
    - persistentDatabase: Database<K, V>
    + Constructor(database: Database<K, V>)
    + get(key: K): V
    + put(key: K, value: V): void
}
```

#### Database Interface & Implementations
```
interface Database<K, V> {
    + save(key: K, value: V): void
    + fetch(key: K): V
}

class InMemoryDatabase<K, V> implements Database<K, V> {
    - dbStore: Map<K, V>
    + save(key: K, value: V): void
    + fetch(key: K): V
}

// Future implementations for SQLDatabase, NoSQLDatabase, etc.
```

#### Client's Code
```
Database<String, String> db = new InMemoryDatabase<>();
Cache<String, String> cache = new Cache<>(db);
cache.put("key1", "value1");
String value = cache.get("key1");
```

#### Example Implementation of InMemoryDatabase
```java
class InMemoryDatabase<K, V> implements Database<K, V> {
    private Map<K, V> dbStore = new HashMap<>();

    public void save(K key, V value) {
        dbStore.put(key, value);
    }

    public V fetch(K key) {
        if (!dbStore.containsKey(key)) {
            dbStore.put(key, "DefaultValue"); // Simulate fetching from a real database
        }
        return dbStore.get(key);
    }
}
```

# V1: Adding Passive TTL-based Invalidations
- Add TTL (Time-To-Live) for cache entries
- After TTL expires, the data is stale and should be fetched from the database again

#### CacheEntry
```java
class CacheEntry<V> {
    - V value;
    - Instant expiryTime;
    + isExpired(): boolean
}
```

#### Updated Cache Class
```java
class Cache<K, V> {
    - cacheData: Map<K, CacheEntry<V>>
    - persistentDatabase: Database<K, V>
    - ttl: int = 5          // TTL in seconds
    + Constructor(database: Database<K, V>, ttl: Duration)
    + get(key: K): V
    + put(key: K, value: V): void
}
```

#### Updated Client's Code
```java
Database<String, String> db = new InMemoryDatabase<>();
Cache<String, String> cache = new Cache<>(db, Duration.ofSeconds(5));
cache.put("key1", "value1");
Thread.sleep(6000); // Wait for 6 seconds to let the TTL expire
String value = cache.get("key1"); // This will fetch from the database again
```

### Why not to use `Map<K, Pair<V, Instant>>` for Cache Data?
We chose a `CacheEntry` class over `Map<K, Map<>>` because:
1. **Future Extensibility**: CacheEntry can easily accommodate additional features (like metadata or versioning) without complicating the structure.
2. **Cleaner Design**: Directly storing TTL in CacheEntry simplifies logic and avoids managing multiple maps.
3. **Better Maintainability**: Encapsulating value and TTL together makes the code easier to extend and modify in the future.


# V2: There can be multiple ways for Cache Invalidation
Please Note: We are only invalidating that value whose key is requested and its TTL has expired.
We are not proactively invalidating all the expired keys.

#### CacheInvalidationStrategy Interface & Implementations
```java
interface CacheInvalidationStrategy<K, V> {
    + isValid(entry: CacheEntry<V>): boolean
}

class TTLBasedInvalidation<K, V> implements CacheInvalidationStrategy<K, V> {
    + isValid(entry: CacheEntry<V>): boolean {
        return !entry.isExpired();
    }
}
```

#### Updated Cache Class
```java
class Cache<K, V> {
    - cacheData: Map<K, CacheEntry<V>>
    - persistentDatabase: Database<K, V>
    - ttl: int = 5          // TTL in seconds
    - invalidationStrategy: CacheInvalidationStrategy<K, V>
    + Constructor(database: Database<K, V>, invalidationStrategy: CacheInvalidationStrategy<K, V>, ttl: Duration)
    + get(key: K): V        // Uses invalidationStrategy to check validity
    + put(key: K, value: V): void
    // Getters
}
```

#### Updated Client's Code
```java
Database<String, String> db = new InMemoryDatabase<>();
CacheInvalidationStrategy<String, String> invalidationStrategy = new TTLBasedInvalidation<>();
Cache<String, String> cache = new Cache<>(db, invalidationStrategy, Duration.ofSeconds(5));
cache.put("key1", "value1");
Thread.sleep(6000); // Wait for 6 seconds to let the TTL expire
String value = cache.get("key1"); // This will fetch from the database again
```


# V3: Writing Strategies
There can be multiple writing strategies:
- Write Through
- Write Back
- Write Around
This is goining to affect the `put` method of Cache.

#### WriteStrategy Interface & Implementations
```java
interface WriteStrategy<K, V> {
    void write(cacheMap: Map<K, CacheEntry<V>>, database: Database<K, V>, key: K, value: V, ttl: int): void;
}

class WriteThrough<K, V> implements WriteStrategy<K, V> {
    @Override
    public void write(cacheMap: Map<K, CacheEntry<V>>, database: Database<K, V>, key: K, value: V, ttl: int) {
        cacheMap.put(key, new CacheEntry<>(value, Instant.now().plusSeconds(ttl)));
        database.save(key, value);
    }
}

class WriteBack<K, V> implements WriteStrategy<K, V> {
    - dirtyMap: ConcurrentMap<K, V> = new ConcurrentHashMap<>();
    @Override
    public void write(cacheMap: Map<K, CacheEntry<V>>, database: Database<K, V>, key: K, value: V, ttl: int) {
        // Only update the cache
        cacheMap.put(key, new CacheEntry<>(value, Instant.now().plusSeconds(ttl)));
        dirtyMap.put(key, value);
        // Database will be updated later (not implemented here)
    }
    - flushDirtyEntriesToDB(): void
}

class WriteAround<K, V> implements WriteStrategy<K, V> {
    @Override
    public void write(cacheMap: Map<K, CacheEntry<V>>, database: Database<K, V>, key: K, value: V, ttl: int) {
        // Write directly to the database, bypassing the cache
        cache.getDatabase().save(key, value);
        // Cache is not updated
    }
}
```

#### Updated Cache Class
```java
class Cache<K, V> {
    - cacheData: Map<K, CacheEntry<V>>
    - persistentDatabase: Database<K, V>
    - ttl: int = 5
    - invalidationStrategy: CacheInvalidationStrategy<K, V>
    - writeStrategy: WriteStrategy<K, V>
    + Constructor(database: Database<K, V>, invalidationStrategy: CacheInvalidationStrategy<K, V>, writeStrategy: WriteStrategy<K, V>)
    + get(key: K): V        // Uses invalidationStrategy to check validity
    + put(key: K, value: V): void  // Uses writeStrategy to write
}

// Changes in the put method
public void put(K key, V value) {
    writeStrategy.write(cacheMap, persistentDatabase, key, value, ttl);
    // rest of the logic remains the same
}
```


#### Updated Client's Code
```java
Database<String, String> db = new InMemoryDatabase<>();
CacheInvalidationStrategy<String, String> invalidationStrategy = new TTLBasedInvalidation<>();
WriteStrategy<String, String> writeStrategy = new WriteThrough<>();
Cache<String, String> cache = new Cache<>(db, invalidationStrategy, writeStrategy);
cache.put("key1", "value1");
Thread.sleep(6000); // Wait for 6 seconds to let the TTL expire
String value = cache.get("key1"); // This will fetch from the database again
```

# V4: Eviction Policies
There can be multiple eviction policies:
- LRU (Least Recently Used)
- LFU (Least Frequently Used)
- FIFO (First In First Out)
- LIFO (Last In First Out)

This is going to affect the `put` method of Cache when the cache reaches its maximum capacity.

#### EvictionPolicy Interface & Implementations
```java
interface EvictionPolicy<K, V> {
    V get(K key);      // lookup + update access order
    void put(K key, V value);  // insert/update + handle eviction internally
}

Implementations for LRU, LFU, FIFO, LIFO would go here.
```

#### Updated Cache Class
```java
class Cache<K, V> {
    - evictionPolicy: EvictionPolicy<K, V>
    - persistentDatabase: Database<K, V>
    - invalidationStrategy: CacheInvalidationStrategy<K, V>
    - writeStrategy: WriteStrategy<K, V>
    + Constructor(database: Database<K, V>, invalidationStrategy: CacheInvalidationStrategy<K, V>, writeStrategy: WriteStrategy<K, V>, evictionPolicy: EvictionPolicy<K, V>)
    + get(key: K): V        // Uses invalidationStrategy to check validity and evictionPolicy to get
    + put(key: K, value: V): void  // Uses writeStrategy to write and evictionPolicy to manage cache size
}

// Changes in the get method
public V get(K key) {
    CacheEntry<V> entry = evictionPolicy.get(key);
    // rest of the logic remains the same
}
```

#### Writing Strategy Update
```java
interface WriteStrategy<K, V> {
    + write(EvictionPolicy<K, V> map, Database<K, V> database, K key, V value, int ttl): void;
}
```

#### Updated Client's Code
```java
Database<String, String> db = new InMemoryDatabase<>();
CacheInvalidationStrategy<String, String> invalidationStrategy = new TTLBasedInvalidation<>();
WriteStrategy<String, String> writeStrategy = new WriteThrough<>();
EvictionPolicy<String, String> evictionPolicy = new LRU<>(3); // Cache capacity
Cache<String, String> cache = new Cache<>(db, invalidationStrategy, writeStrategy, evictionPolicy);
cache.put("key1", "value1");
cache.put("key2", "value2");
cache.put("key3", "value3");
cache.put("key4", "value4"); // This will evict the least recently used
Thread.sleep(6000); // Wait for 6 seconds to let the TTL expire
String value = cache.get("key1"); // This will fetch from the database again if key1 was evicted
```

# V5: Reading Strategies
There can be multiple reading strategies:
- Cache-Aside (Lazy Loading)
- Read-Through Cache
This is going to affect the `get` method of Cache.

#### ReadStrategy Interface & Implementations
```java
interface ReadStrategy<K, V> {
    + read(EvictionPolicy<K, V> cachedata, Database<K, V> database, Cache<K, V> cache, K key): V
}

class ReadThrough<K, V> implements ReadStrategy<K, V> {
    @Override
    public V read(EvictionPolicy<K, V> cachedata, Database<K, V> database, Cache<K, V> cache, K key) {
        CacheEntry<V> entry = cachedata.get(key);

        if (entry == null || !entry.isExpired()) {
            // Fetch from database if not present or invalid (expired)
            V value = database.fetch(key);
            cache.put(key, value); // Update cache
            return value;
        }
        return entry.getValue();
    }
}

class CacheAside<K, V> implements ReadStrategy<K, V> {
    @Override
    public V read(EvictionPolicy<K, V> cachedata, Database<K, V> database, Cache<K, V> cache, K key) {
        CacheEntry<V> entry = cachedata.get(key);

        if (entry == null || !entry.isExpired()) {
            return null; // Client must fetch from DB and write manually
        }

        return entry.getValue();
    }
}
```

#### Updated Cache Class
```java
class Cache<K, V> {
    - readStrategy: ReadStrategy<K, V>
    - evictionPolicy: EvictionPolicy<K, V>
    - persistentDatabase: Database<K, V>
    - invalidationStrategy: CacheInvalidationStrategy<K, V>
    - writeStrategy: WriteStrategy<K, V>
    + Constructor(database: Database<K, V>, invalidationStrategy: CacheInvalidationStrategy<K, V>, writeStrategy: WriteStrategy<K, V>, evictionPolicy: EvictionPolicy<K, V>, readStrategy: ReadStrategy<K, V>)
    + get(key: K): V        // Uses readStrategy to read
    + put(key: K, value: V): void  // Uses writeStrategy to write
```


#### Updated Client's Code
```java
Database<String, String> db = new InMemoryDatabase<>();
CacheInvalidationStrategy<String, String> invalidationStrategy = new TTLBasedInvalidation<>();
WriteStrategy<String, String> writeStrategy = new WriteThrough<>();
EvictionPolicy<String, String> evictionPolicy = new LRU<>(3); // Cache capacity
ReadStrategy<String, String> readStrategy = new ReadThrough<>();
Cache<String, String> cache = new Cache<>(db, invalidationStrategy, writeStrategy, evictionPolicy, readStrategy);
cache.put("key1", "value1");
Thread.sleep(6000); // Wait for 6 seconds to let the TTL expire
String value = cache.get("key1"); // This will fetch from the database again
```



# V6: Passive Prefetching
- Prefetching is a technique where the cache proactively loads data from the underlying data source before it is actually requested by the client.
- This is basically optimizing the db calls.
- DB calls are made by the read strategy.
- So our read strategy will be updated to support prefetching.

There can be multiple prefetching strategies:
- Locality of Reference
- Sequential Access Pattern
- Probability-Based(AI/ML) Prefetching

#### PrefetchingStrategy Interface & Implementations
```java
interface PrefetchingStrategy<K, V> {
    + prefetch(cache: Cache<K, V>, key: K): List<K>
}

Implementations for LocalityOfReference, SequentialAccessPattern, ProbabilityBasedPrefetching would go here.
```

#### Update the Database Interface
```java
interface Database<K, V> {
    + save(key: K, value: V): void
    + fetch(key: K): V
    + bulkFetch(keys: List<K>): Map<K, V>   // New method for prefetching
    + bulkSave(entries: Map<K, V>): void
}
```

#### Update Cache Class
```java
class Cache<K, V> {
    - prefetchingStrategy: PrefetchingStrategy<K, V>
    - readStrategy: ReadStrategy<K, V>
    - evictionPolicy: EvictionPolicy<K, V>
    - persistentDatabase: Database<K, V>
    - invalidationStrategy: CacheInvalidationStrategy<K, V>
    - writeStrategy: WriteStrategy<K, V>
    + Constructor(database: Database<K, V>, invalidationStrategy: CacheInvalidationStrategy<K, V>, writeStrategy: WriteStrategy<K, V>, evictionPolicy: EvictionPolicy<K, V>, readStrategy: ReadStrategy<K, V>, prefetchingStrategy: PrefetchingStrategy<K, V>)
    + get(key: K): V        // Uses readStrategy to read (with prefetching)
    + put(key: K, value: V): void  // Uses writeStrategy to write
}
```

#### Update ReadStrategy Interface & Implementations
```java
interface ReadStrategy<K, V> {
    + read(EvictionPolicy<K, V> cachedata, Database<K, V> database, Cache<K, V> cache, K key): V
        // Note: Use cache.prefetchingStrategy.prefetch(cache, key) to get keys to prefetch
}
```

#### Updated ReadThrough Class
```java
class ReadThrough<K, V> implements ReadStrategy<K, V> {
    @Override
    public V read(EvictionPolicy<K, V> cachedata, Database<K, V> database, Cache<K, V> cache, K key) {
        CacheEntry<V> entry = cachedata.get(key);
        if (entry == null || !entry.isExpired()) {
            // Prefetch additional keys with the requested key
            List<K> keysToPrefetch = cache.getPrefetchingStrategy().prefetch(cache, key);
            // Add the requested key to the list if not already present
            if (!keysToPrefetch.contains(key)) {
                keysToPrefetch.add(key);
            }
            if (keysToPrefetch != null && !keysToPrefetch.isEmpty()) {
                Map<K, V> prefetchedData = cache.getPersistentDatabase().bulkFetch(keysToPrefetch);
                for (Map.Entry<K, V> entrySet : prefetchedData.entrySet()) {
                    cache.put(entrySet.getKey(), entrySet.getValue());
                }
            }

            return value;
        }
        return entry.getValue();
    }
}
```

#### Updated Client's Code
```java
Database<String, String> db = new InMemoryDatabase<>();
CacheInvalidationStrategy<String, String> invalidationStrategy = new TTLBasedInvalidation<>();
WriteStrategy<String, String> writeStrategy = new WriteThrough<>();
EvictionPolicy<String, String> evictionPolicy = new LRU<>(3); // Cache capacity
ReadStrategy<String, String> readStrategy = new ReadThrough<>();
PrefetchingStrategy<String, String> prefetchingStrategy = new LocalityOfReference<>();

Cache<String, String> cache = new Cache<>(db, invalidationStrategy, writeStrategy, evictionPolicy, readStrategy, prefetchingStrategy);
cache.put("key1", "value1");
Thread.sleep(6000); // Wait for 6 seconds to let the TTL expire
String value = cache.get("key1"); // This will fetch from the database again along with
// prefetching other related keys based on the prefetching strategy
```

# V7: Consistent Hashing for Distribution and Scalability
We are renaming Cache to CacheNode.
CacheNode will have all the previous functionalities.
- evictionPolicy
- persistentDatabase
- invalidationStrategy
- writeStrategy
- readStrategy


#### HorizontalScaling Interface
```java
interface HorizontalScaling<K, V> {
    void addNode(CacheNode<K, V> node);
    void removeNode(CacheNode<K, V> node);
    CacheNode<K, V> getNodeForKey(K key);
}

class ConsistentHashing<K, V> implements HorizontalScaling<K, V> {
    - ring: TreeMap<Long, CacheNode<K, V>>
    - numberOfReplicas: int
    - md: MessageDigest
    + Constructor(numberOfReplicas: int)
    + addNode(node: CacheNode<K, V>): void
    + removeNode(node: CacheNode<K, V>): void
    + getNodeForKey(key: K): CacheNode<K, V>
    - generateHash(key: String): long
}
```

#### CacheNode Class (formerly Cache)
```java
class CacheNode<K, V> {
    - evictionPolicy: EvictionPolicy<K, V>
    - persistentDatabase: Database<K, V>
    - invalidationStrategy: CacheInvalidationStrategy<K, V>
    - writeStrategy: WriteStrategy<K, V>
    - readStrategy: ReadStrategy<K, V>
    - prefetchingStrategy: PrefetchingStrategy<K, V>
    + Constructor(nodeId: String, database: Database<K, V>, invalidationStrategy: CacheInvalidationStrategy<K, V>, writeStrategy: WriteStrategy<K, V>, readStrategy: ReadStrategy<K, V>, evictionPolicy: EvictionPolicy<K, V>, prefetchingStrategy: PrefetchingStrategy<K, V>)
    + get(key: K): V        // Uses readStrategy to read
    + put(key: K, value: V): void  // Uses writeStrategy to write
}
```

#### CacheSystem Class (Top-Level Controller)
```java
class CacheSystem<K, V> {
    - scalingStrategy: HorizontalScaling<K, V>
    + Constructor(scalingStrategy: HorizontalScaling<K, V>)
    + addNode(node: CacheNode<K, V>): void
    + removeNode(node: CacheNode<K, V>): void
    + get(key: K): V
    + put(key: K, value: V): void
}
```

#### Updated Client's Code
```java
Database<String, String> db = new InMemoryDatabase<>();
CacheInvalidationStrategy<String, String> invalidationStrategy = new TTLBasedInvalidation<>();
WriteStrategy<String, String> writeStrategy = new WriteThrough<>();
EvictionPolicy<String, CacheEntry<String>> evictionPolicy = new LRUEvictionPolicy<>(3);
ReadStrategy<String, String> readStrategy = new ReadThrough<>();
PrefetchingStrategy<String, String> prefetchingStrategy = new LocalityOfReference<>();

CacheNode<String, String> node1 = new CacheNode<>("Node1", db, invalidationStrategy, writeStrategy, readStrategy, evictionPolicy, prefetchingStrategy);
CacheNode<String, String> node2 = new CacheNode<>("Node2", db, invalidationStrategy, writeStrategy, readStrategy, evictionPolicy, prefetchingStrategy);

HorizontalScaling<String, String> scaling = new ConsistentHashing<>(100);
CacheSystem<String, String> cacheSystem = new CacheSystem<>(scaling);

cacheSystem.addNode(node1);
cacheSystem.addNode(node2);

// Client interacts only with CacheSystem
cacheSystem.put("user:1", "Alice");
cacheSystem.put("user:2", "Bob");

System.out.println(cacheSystem.get("user:1")); // Alice
System.out.println(cacheSystem.get("user:2")); // Bob
```


# V8: Request Collapsing (Request Coalescing)
- When multiple requests for the same key arrive concurrently, only one request should fetch the data from the database, while others wait for the result.
- This optimization reduces redundant database calls and improves performance.

- We can have two types of collapsing caches:
  - Asynchronous Collapsing Cache
  - Synchronous Collapsing Cache

#### What is the difference between Asynchronous and Synchronous Collapsing Cache?
- In an **Asynchronous Collapsing Cache**, when multiple requests for the same key arrive, they are queued and processed in the background. The first request triggers a database fetch, and subsequent requests wait for the result to be available. This approach is non-blocking and allows other operations to continue while waiting for the data.
- In a **Synchronous Collapsing Cache**, when multiple requests for the same key arrive, they are processed in a blocking manner. The first request triggers a database fetch, and subsequent requests are blocked until the result is available. This approach ensures that all requests for the same key are handled in a sequential manner, which can lead to increased latency for those requests.

#### My Idea:
- We will be implementing Asynchronous Collapsing Cache.
- We will use `CompletableFuture` to handle asynchronous operations.
- We will maintain a `ConcurrentMap<K, CompletableFuture<V>>` to track ongoing requests for each key.

