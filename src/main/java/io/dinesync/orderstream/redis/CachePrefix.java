package io.dinesync.orderstream.redis;

public enum CachePrefix {

    ORDER("order");

    private final String prefix;

    CachePrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getKey(String key) {
        return prefix.concat(":").concat(key);
    }

}
