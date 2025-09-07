package com.spring.memodb.store;

import com.spring.memodb.resp.RespType;

/**
 * Represents an entry in the in-memory key-value store, encapsulating the
 * stored value along with metadata such as timestamp and optional expiry time.
 */
public record Entry(
        RespType value,
        long timestamp,
        long expiryTime
        ) {

    /**
     * Constructor for creating an Entry without expiry time.
     *
     * @param value
     */
    public Entry(RespType value) {
        this(value, System.currentTimeMillis(), 0);
    }

    /**
     * Constructor for creating an Entry with a specified expiry time.
     *
     * @param value
     * @param expiryTime
     */
    public Entry(RespType value, long expiryTime) {
        this(value, System.currentTimeMillis(), expiryTime);
    }

    /**
     * Checks if the entry has expired based on the current time and its expiry
     * time.
     *
     * @return
     */
    public boolean isExpired() {
        if (expiryTime <= 0) {
            return false; // No expiry set
        }
        return System.currentTimeMillis() > (timestamp + expiryTime);
    }

}
