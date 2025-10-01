# D. Cache Eviction and Invalidation

## 1. Eviction vs Invalidation

- **Eviction**: 
  - Automatic removal of items from cache when space is limited or based on a policy (e.g., LRU, LFU).  
  - Happens due to **capacity constraints**.  
  - Example: Redis removes the least recently used key when memory is full.  

- **Invalidation**: 
  - Explicit removal/marking of items as stale to maintain consistency with the database.  
  - Happens due to **data updates or expiration rules**.  
  - Example: Removing a product’s cached details when its price is updated in the DB.  

**Key Difference:**  
- Eviction = cache removes items **to free space**.  
- Invalidation = cache removes items **to keep data fresh**.  



## 2. Cache Eviction Policies

These decide **which data to remove** when the cache is full:  

1. **LRU (Least Recently Used)**  
   - Removes the item that hasn’t been used for the longest time.  
   - Example: Browser tabs → the oldest unused one gets discarded first.  

2. **LFU (Least Frequently Used)**  
   - Removes the item with the fewest accesses.  
   - Example: Songs in a music app that no one listens to anymore get evicted first.  

3. **FIFO (First In, First Out)**  
   - Removes the earliest inserted item.  
   - Example: Queue system → the oldest cached item leaves first.  

4. **LIFO (Last In, First Out)**  
   - Removes the most recently inserted item.  
   - Rarely used.  
   - Example: Stack-like behavior (last pushed gets popped).  


## 3. Cache Invalidation Techniques

These ensure cache consistency with the underlying database:  

1. **Manual Invalidation**  
   - App explicitly deletes/refreshes cache entries when DB changes.  
   - Example: Amazon deletes cached product details when inventory/price changes.  

2. **Time-based Expiry (TTL)**  
   - Items are invalidated automatically after a set time.  
   - Example: News articles expire every 5 minutes in cache. 
   - Used in: CDNs, Redis, Memcached.   

3. **Write Invalidation**  
   - Cache updated/invalidated immediately on DB writes (common in write-through/write-around).  
   - Example: User profile update → cache entry removed so next read reloads fresh data.  

4. **Event-driven Invalidation**  
   - Cache entries invalidated when certain events trigger.  
   - Example: CDN purges cache when a new website deployment event occurs.  


## 4. Summary Table

| Concept | Trigger | Purpose | Example |
|---------|---------|---------|---------|
| **Eviction** | Cache is full (capacity limit) | Free space | Redis removes LRU key |
| **Invalidation** | Data changes / TTL expiry | Keep cache fresh | Amazon removes outdated product cache |
| **LRU Eviction** | Oldest unused item removed | Efficient memory use | Redis, Memcached |
| **TTL Invalidation** | Time expiry | Auto-refresh data | API caching for 10 mins |

---
