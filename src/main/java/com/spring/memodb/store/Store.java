package com.spring.memodb.store;

import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

/**
 * Singleton class representing an in-memory key-value store. It provides
 * thread-safe methods to get, put, and remove entries from the store.
 */
@Slf4j
public class Store {

    private volatile static Store instance; // singleton instance
    private final ConcurrentHashMap<String, Entry> store = new ConcurrentHashMap<>(); // thread-safe map

    /**
     * Private constructor to prevent instantiation from outside the class.
     */
    private Store() {
    }

    /**
     * Gets the singleton instance of the Store. If the instance does not exist,
     * it is created in a thread-safe manner.
     *
     * @return
     */
    public static Store getInstance() {
        if (instance != null) {
            return instance;
        }

        synchronized (Store.class) {
            if (instance == null) {
                instance = new Store();
            }
            return instance;
        }
    }

    /**
     * Gets the Entry associated with the given key.
     *
     * @param key
     * @return the Entry if found, null otherwise
     */
    public Entry get(String key) {
        log.debug("Store: Getting entry for key: {}", key);

        Entry entry = store.get(key);

        if (entry == null) {
            log.debug("Store: No entry found for key: {}", key);
            return null;
        }

        if (entry.isExpired()) {
            log.debug("Store: Entry for key: {} has expired. Removing it from store.", key);
            store.remove(key);
            return null;
        }

        log.debug("Store: Entry found for key: {}", key);
        return entry;
    }

    /**
     * Puts the given Entry into the store with the specified key.
     *
     * @param key
     * @param entry
     */
    public void put(String key, Entry entry) {
        log.debug("Store: Putting entry for key: {}", key);

        store.put(key, entry);
    }

    /**
     * Removes the Entry associated with the given key from the store.
     *
     * @param key
     */
    public void remove(String key) {
        log.debug("Store: Removing entry for key: {}", key);

        store.remove(key);
    }

    /**
     * Checks if the store contains the given key.
     *
     * @param key
     * @return true if the key exists, false otherwise
     */
    public boolean containsKey(String key) {

        return store.containsKey(key);
    }

    /**
     * Gets the current size of the store.
     *
     * @return the number of entries in the store
     */
    public int size() {
        return store.size();
    }

    /**
     * Cleans up expired entries from the store. This method iterates through
     * the entries and removes those that have expired.
     */
    public void cleanupExpiredEntries() {
        store.entrySet().removeIf((entry) -> {
            if (entry.getValue().isExpired()) {
                log.debug("Removing expired entry for key: {}", entry.getKey());
                return true;
            }
            return false;
        });
    }
}
