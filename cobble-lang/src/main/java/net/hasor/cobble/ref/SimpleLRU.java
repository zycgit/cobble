/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.ref;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 简单的基于 LinkedHashMap 的 LRU 实现
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-04-19
 */
public class SimpleLRU<K, V> extends LinkedHashMap<K, V> {
    private final int MAX_CACHE_SIZE;

    public SimpleLRU(int cacheSize) {
        super((int) Math.ceil(cacheSize / 0.75) + 1, 0.75f, true);
        MAX_CACHE_SIZE = cacheSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > MAX_CACHE_SIZE;
    }
}
