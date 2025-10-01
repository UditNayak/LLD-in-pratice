# A. Cache Fundamentals

## 1. What is Cache?
A cache is a temporary fast storage that reduces data access time by storing frequently used data closer to the application.

### Real-world analogy:

#### School Locker
Instead of carrying all your textbooks from home (database) every day, you keep the most frequently used ones in your locker (cache). It saves time and effort.

#### Kitchen Pantry
You keep frequently used ingredients (like spices, oil, etc.) in your kitchen pantry (cache) instead of going to the grocery store (database) every time you need them.

## 2. Cache vs Database
| Aspect            | Cache                                 | Database                                   |
| ----------------- | ------------------------------------- | ------------------------------------------ |
| **Purpose**       | Speed up data retrieval (temporary)   | Store data permanently (persistent)        |
| **Storage Type**  | In-memory (RAM, sometimes SSD)        | Disk-based (HDD, SSD, distributed storage) |
| **Data Lifetime** | Short-lived (TTL, eviction policies)  | Long-lived (until explicitly deleted)      |
| **Consistency**   | May serve slightly stale data         | Source of truth (always consistent)        |
| **Access Speed**  | Nanoseconds–milliseconds              | Milliseconds–seconds                       |
| **Use Case**      | Reduce latency, handle high read load | Reliable data persistence                  |

## 3. Varieties of Cache (with Examples)
| Category                           | Example Services / Systems                                       | Description                                                                                       |
| ---------------------------------- | ---------------------------------------------------------------- | ------------------------------------------------------------------------------------------------- |
| **In-Memory Cache**                | Redis, Memcached                                                 | Fast, general-purpose caching in RAM; ideal for application-level and distributed caching.        |
| **Cloud-managed Cache**            | AWS ElastiCache, Google Cloud Memorystore, Azure Cache for Redis | Fully managed cache services for scalability and high availability.                               |
| **CDN (Content Delivery Network)** | Cloudflare, Akamai, AWS CloudFront                               | Edge servers cache static/dynamic web content close to users to reduce latency.                   |
| **Hardware-level Cache**           | CPU L1/L2/L3 caches                                              | Stores frequently used CPU instructions/data in hardware registers for nanosecond-level speed.    |
| **Web Browser Cache**              | Chrome/Firefox browser cache, Service Workers                    | Stores static assets (images, JS, CSS) locally to avoid re-fetching from server.                  |
| **Application-level Cache**        | Spring Boot @Cacheable, Guava Cache, Django cache framework      | Built directly into apps to avoid repeated expensive computations or DB lookups.                  |
| **Database-level Cache**           | MySQL Query Cache, PostgreSQL Buffer Cache, Oracle Result Cache  | Databases themselves maintain memory caches for frequently accessed rows/queries.                 |
| **Distributed Cache**              | Redis Cluster, Hazelcast, Apache Ignite                          | Cache spread across multiple servers; provides scalability, fault tolerance, and high throughput. |

## 4. Why Distributed Cache?

> What happens when we scale a cache?

As applications grow, the cache may not handle the increasing load. To deal with this, we can scale the cache system in two ways:

### A. Vertical Scaling (Scaling Up)
- Add more resources (CPU, RAM, SSD) to a single cache server.
- **Pros**: Simple to implement, no changes to application code.
- **Cons**:
    - Limited by hardware capacity. (e.g., max RAM on a single machine)
    - Single point of failure.
- **Use Case**: Suitable for small to medium workloads where simplicity is key.

**Analogy:**
- Imagine a **restaurant kitchen**.
- Vertical scaling = hiring more chefs and buying bigger stoves in the **same kitchen**. Eventually, the kitchen gets too crowded, and you can’t expand further.

### B. Horizontal Scaling (Scaling Out)
- Add more cache servers (nodes) to distribute the load.
- Distribute data and traffic across them (using **consistent hashing** or sharding).

**Analogy:**
Horizontal scaling = instead of just one big restaurant, you **open new branches** across the city. Customers go to the nearest branch, reducing wait time and spreading the load.

#### Distributed Cache = Horizontal Scaling
A distributed cache is essentially horizontal scaling of the cache layer:
- Multiple nodes → more capacity.
- No single point of failure.
- Requests balanced across nodes.


## 5. Cache Terminologies
A. **Cache Hit**: When requested data is found in the cache (fast response).
B. **Cache Miss**: When requested data is not found in cache → must fetch from DB (slower).
C. **Stale Data**: Data in cache that is outdated compared to the source of truth (DB).
D. **Cache Invalidation**: Process of removing or updating stale data in the cache.
E. **TTL (Time-To-Live)**: Duration data remains in cache before being automatically removed.
F. **Eviction**: Removal of cache entries to free up space (based on LRU, LFU, etc.).
G. **Hot Key**: A frequently accessed cache entry that can lead to performance bottlenecks if not managed properly.
H. **Warm Cache**: A cache that has been populated with data (ready for use).
I. **Cold Cache**: An empty cache that needs to be populated (initial state).

