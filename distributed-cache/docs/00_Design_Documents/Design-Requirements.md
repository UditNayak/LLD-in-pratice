# Design a Distributed Key-Value Cache [Google Interview Question]

### Feature 1: Basic Operations
- get(key)
- put(key, value)

  **Future enhancements**:
  - delete(key)
  - update(key, value)

### Feature 2: Cache Invalidation
There are several strategies for cache invalidation:
- TTL (Time to Live) for each key-value pair
- Manual Invalidation (e.g., through an API call)
- Event-based Invalidation (e.g., based on changes in the underlying data source)

In this design, we will implement TTL-based invalidation.

But our design will be flexible enough to allow for future enhancements like manual or event-based invalidation.

### Feature 3: Eviction Policies
There are several cache eviction policies to choose from:
- LRU (Least Recently Used)
- LFU (Least Frequently Used)
- FIFO (First In First Out)
- LIFO (Last In First Out)

In this design, we will implement the LRU eviction policy.
But our design will be flexible enough to allow for future enhancements like LFU, FIFO, or LIFO.

### Feature 4: Writing Strategies
There are several writing strategies to choose from:
- Write Through
- Write Back
- Write Around

In this design, we will implement the Write Through strategy.
But our design will be flexible enough to allow for future enhancements like Write Back or Write Around.

### Feature 5: Reading Strategies
There are several reading strategies to choose from:
- Cache-Aside (Lazy Loading)
- Read-Through Cache

In this design, we will implement the Cache-Aside strategy.
But our design will be flexible enough to allow for future enhancements like Read-Through Cache.

### Feature 6: Distribution and Scalability
We will use consistent hashing to distribute keys across multiple cache nodes.

### Workflow:
- Entity 1: Application (Client)
  - We can assume that the application is a our client that interacts with the cache.
  - It is going to query 2 things:
    - get(key)
    - put(key, value)
- Entity 2: Cache
  - Your cache is not the Load Balancer.
  - It has a load balancer, it has multiple cache nodes where the data is actually stored.
  - It has the strategy to distribute the data across multiple cache nodes.
  - It has the strategy of writing.
  - It has the strategy of reading.
  - It has the strategy of eviction.
  - It has the strategy of invalidation.
Now the client need not know whether there is a load balancer or not.
This client need not know how many cache nodes are there.
This client need not know what is the strategy of writing, reading, eviction, invalidation.
This client should be completely agnostic about what is happening inside the cache.

- Another thing which is not Explicit it is implicit is that:
  - If the data is not present in the cache, then the cache should go and fetch the data from the underlying data source.
  - Database can also be of Different types.
    - SQL Database
    - NoSQL Database
  - The system should be agnostic about what is the underlying data base is used. So definitly we will be using the interface to interact with the underlying data source. (Adapter Pattern)