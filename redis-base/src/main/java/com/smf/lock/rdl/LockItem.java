package com.smf.lock.rdl;

/**
 * LockItem TODO
 *
 * @author hf
 * @date 2024/1/9 11:06:12
 */
public class LockItem { private final String key;
    private final String value;

    public LockItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
