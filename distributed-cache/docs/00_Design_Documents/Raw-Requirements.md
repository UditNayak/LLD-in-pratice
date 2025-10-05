# Design a Distributed Key-Value Cache [Google Interview Question]

- Most of the cache system are going to provide the following functionalities:
  - get(key)
  - put(key, value)

    Future enhancements:
    - delete(key)
    - update(key, value)

- TTL (Time to Live) for each key-value pair

- Writing strategies:
  - Write Through (use this for now)
  - Write Back
  - Write Around

- Reading strategies:
    - Cache Aside (use this for now)
    - Read Through
    - Write Through

- Distribution Strategy:
  - Consistent Hashing (Use this for now)
  - (Check other strategies too)

- Cache Eviction Policies:
    - LRU (Least Recently Used) (Use this for now)
    - LFU (Least Frequently Used)
    - FIFO (First In First Out)
    - LIFO (Last In First Out)

- Request Collapsing

- Prefetching
    - Locality of reference (use this for now)
    - (Check other strategies too)

