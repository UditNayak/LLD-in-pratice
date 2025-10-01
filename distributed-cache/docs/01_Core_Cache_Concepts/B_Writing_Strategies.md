# B. Writing Strategies

## 1. Write-Through Cache
**Defination**: Data is written **to cache and database simultaneously**.
- Ensures cache is always consistent with the database.
- Slightly slower writes (because two writes happen).
**When to Use**:
    - When strong consistency is critical (e.g., banking, payments).
    - **Read-heavy applications**: where reads are frequent but writes are not too heavy.

## 2. Write-Back (Write-Behind)
**Defination**: Data is first written to the cache → database update happens asynchronously (later).
- Very fast writes.
- Risk of data loss if cache node fails before flushing to DB.
**When to Use**: (Write-Heavy with some tolerance for eventual consistency)
    - When high write performance is needed (e.g., logging, analytics).
    - Applications where slight delay in persistence is acceptable.

## 3. Write-Around Cache
**Defination**: Data is written **only to the database**, not to cache. Cache is updated only **when that data is read later**.
- Prevents cache pollution with rarely used data.
- May cause a cache miss immediately after a write.
**When to Use**: 
    - When most data is rarely read.
    - Applications with **heavy write load but low read reuse**.

## 4. Summary of Write Strategies
| Strategy          | Speed (Write)          | Consistency | Risk of Data Loss         | Best Use Case                       |
| ----------------- | ---------------------- | ----------- | ------------------------- | ----------------------------------- |
| **Write-Through** | Slower (double write)  | Strong      | None                      | Financial systems, shopping carts   |
| **Write-Back**    | Fast                   | Eventual    | Possible (if cache fails) | Logging, analytics, high-write apps |
| **Write-Around**  | Medium (DB write only) | Strong      | None                      | Rarely-read data, heavy write apps  |

## References
[Top Caching Strategies](https://blog.bytebytego.com/p/top-caching-strategies)