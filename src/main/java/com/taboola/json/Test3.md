## Question 3:
You're tasked with writing a spec for a generic local cache with the following property: if the cache is
asked for a key that it doesn't contain, it should fetch the data using an externally provided function
that reads the data from another source (eg: a database).
What features do you think such a cache should offer? How, in general lines, would you implement
it?

## What is local cache feature?
1. TTL
2. apis: get, delete, put

## How to implement?
1. how to implement ttl expiry mechanism?
Use a map to store the ttl for each key.
For each get key, check the existence of the key and check that the key is not expired.
If it is expired, clean it.

## Implementation
```java
public interface Cache<K, V> {
    V get(K key);
    void delete(K key);
    void put(K key, V value);
}

public abstract class LocalCache<K, V> implements Cache<K, V> {
    protected Long ttlMillis;
    protected DataSource dataSource;
    protected Map<K, V> keyValueMap = new ConcurrentHashMap<>();
    // This stores the ttl in millis for each key.
    protected Map<K, Long> expirationTimes = new ConcurrentHashMap<>();

    public LocalCache(DataSource dataSource, Long ttlMillis) {
        this.dataSource = dataSource;
        this.ttlMillis = ttlMillis;
    }

    @Override
    public V get(K key) {
        // Check if entry exists and is not expired
        if (keyValueMap.containsKey(key)) {
            Long expirationTime = expirationTimes.get(key);
            if (expirationTime == null || System.currentTimeMillis() < expirationTime) {
                return keyValueMap.get(key);
            } else {
                // Entry expired, remove it
                delete(key);
            }
        }

        // Fetch from data source when cache miss
        V value = dataSource.get(key);
        if (value != null) {
            this.put(key, value);
        }
        return value;
    }

    @Override
    public void delete(K key) {
        keyValueMap.remove(key);
        expirationTimes.remove(key);
    }

    @Override
    public void put(K key, V value) {
        keyValueMap.put(key, value);
        if (ttlMillis != null) {
            expirationTimes.put(key, System.currentTimeMillis() + ttlMillis);
        }
    }
}

public class EmployeeCache extends LocalCache<String, Person> {
    public EmployeeCache(EmployeeDataSource dataSource, Long ttlMillis) {
        super(dataSource, ttlMillis);
    }
}
```