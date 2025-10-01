# C. Reading Strategies

## 1. Cache-Aside (Lazy Loading)
**Definition**: Application checks the cache first → if data is not found (cache miss), it loads from the database and updates the cache.

### Steps:
1. Application requests data.
2. Application checks the cache.
3. If data is found (cache hit), return it.
4. If data is not found (cache miss), application fetches the data from the database.
5. Application updates the cache with the fetched data (with TTL if applicable).
6. Return the data to the application.

### Example:
- **Netflix**: Frequently accessed video metadata (titles, thumbnails) is lazily loaded into Redis from the DB.  
- **Amazon**: Product details are pulled into cache only when customers search or view them.  

**Pros:**  
- Only frequently accessed data is cached (efficient).  
- Simple and flexible to implement.  

**Cons:**  
- First request for data is always a cache miss (slower).  
- Cache can become stale if data in DB changes (requires TTL or invalidation).

### When to Use:
- Most common strategy, simple to implement. 
- When application can tolerate occasional cache misses. 
- Read-heavy workloads where data does not change very frequently.

## 2. Read-Through Cache
**Definition**: Application always queries the cache. If data is missing, the **cache itself** fetches it from the database and stores it before returning to the app. 

### Steps:
1. Application requests data.
2. Cache checks for the data.
3. If data is found (cache hit), return it.
4. If data is not found (cache miss), cache fetches the data from the database.
5. Cache updates itself with the fetched data (with TTL if applicable).
6. Return the data to the application.

### Example:
- **Content Delivery Networks (CDNs)** like **Cloudflare** or **Akamai**: When a file is not cached at the edge server, the CDN itself fetches from the origin server, stores it, and then serves future requests.  
- **Spring Boot with Redis Cache Manager**: Framework handles DB fetch + cache population automatically. 

**Pros:**  
- Simplifies application code (no need to handle DB fetches).  
- Provides consistent caching layer. 

**Cons:**  
- More dependency on cache provider (must support DB loading logic).  
- Cache might store unnecessary data if auto-loaded for one-time queries.

### When to Use:
- When you want simplicity for the application (no manual DB fetch logic).
- Suitable when cache library/service supports auto-loading from DB.
- Abstracts database access away from the application.
- Cache provider handles loading logic. 

## 3. Summary of Read Strategies
| Strategy        | Steps | Pros | Cons | Real-life Examples |
|-----------------|-------|------|------|--------------------|
| **Cache-Aside** | App → Cache → (DB if miss) → Update cache → Return | Efficient, simple | First request = miss, possible stale data | Netflix (metadata), Amazon (product details) |
| **Read-Through** | App → Cache → Cache fetches DB if miss → Update cache → Return | App code is simpler, transparent | Needs cache support, risk of cache pollution | CDNs (Cloudflare, Akamai), Spring Boot + Redis |


## References
- [Top Caching Strategies](https://blog.bytebytego.com/p/top-caching-strategies)