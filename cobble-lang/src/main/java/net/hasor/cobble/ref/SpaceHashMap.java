/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.cobble.ref;
import net.hasor.cobble.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 可以将多个 Map 合并成一个 Map 对象进行操作，同时每个 Map 可以根据名字独立操作它。
 * @version : 2016-07-17
 * @author 赵永春 (zyc@hasor.net)
 */
public class SpaceHashMap<K, T> {
    protected Map<String, Map<K, T>> spaceMap = new LinkedHashMap<>();

    /** 将一个值保存到一个命名空间下，如果命名空间不存在则创建一个。 */
    public T put(final String space, final K key, final T value) {
        Map<K, T> spaceMap = this.spaceMap.get(space);
        if (spaceMap == null) {
            spaceMap = new ConcurrentHashMap<>();
            this.spaceMap.put(space, spaceMap);
        }
        return spaceMap.put(key, value);
    }

    /** 将一个map加入或追加到一个命名空间下，如果命名空间不存在则创建一个。 */
    public void putAll(final String space, final Map<K, T> newMap) {
        Map<K, T> spaceMap = this.spaceMap.get(space);
        if (spaceMap == null) {
            spaceMap = new ConcurrentHashMap<>();
            this.spaceMap.put(space, spaceMap);
        }
        spaceMap.putAll(newMap);
    }

    /** 确认 key 所在的命名空间 */
    public List<T> get(final K key) {
        List<T> findVal = new ArrayList<>();
        for (Map<K, T> map : this.spaceMap.values()) {
            T val = map.get(key);
            if (val != null) {
                findVal.add(val);
            }
        }
        return findVal;
    }

    /** 返回指定命名空间下的 key 值 */
    public T get(final String space, final K key) {
        Map<K, T> map = this.spaceMap.get(space);
        if (map == null) {
            return null;
        } else {
            return map.get(key);
        }
    }

    /** 删除指定命名空间下的 key 值 */
    public T remove(String space, K key) {
        Map<K, T> spaceMap = this.spaceMap.get(space);
        if (spaceMap != null) {
            return spaceMap.remove(key);
        }
        return null;
    }

    /** 清空所有命名空间下的 key 值 */
    public void removeAll(final K key) {
        for (Map<K, T> mapItem : this.spaceMap.values()) {
            mapItem.remove(key);
        }
    }

    /** 命名空间集合 */
    public Set<String> spaceSet() {
        return this.spaceMap.keySet();
    }

    /** 所有命名空间下的所有 Key 集合 */
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (Map<K, T> mapItem : this.spaceMap.values()) {
            keys.addAll(mapItem.keySet());
        }
        return keys;
    }

    /** 某个命名空间下的所有 Key 集合 */
    public Set<K> keySet(String space) {
        Map<K, T> map = this.spaceMap.get(space);
        if (map != null) {
            return map.keySet();
        }
        return new HashSet<>();
    }

    /** 删除某个命名空间 */
    public void deleteSpace(String space) {
        this.spaceMap.remove(space);
    }

    /** 删除所有命名空间 */
    public void deleteAllSpace() {
        this.spaceMap.clear();
    }

    /** 所有命名空间下 key 的总数（去重） */
    public int size() {
        Set<K> keys = new HashSet<>();
        for (Map<K, T> map : this.spaceMap.values()) {
            keys.addAll(map.keySet());
        }
        return keys.size();
    }

    /** 某个命名空间下 key 的总数 */
    public int size(String space) {
        Map<K, T> map = this.spaceMap.get(space);
        if (map == null) {
            return 0;
        } else {
            return map.size();
        }
    }

    /** 将 space 下的数据独立出一个新的 SpaceHashMap */
    public SpaceHashMap<K, T> cloneSpace(final String space) {
        SpaceHashMap<K, T> spaceMap = new SpaceHashMap<>();
        Map<K, T> dataMap = this.spaceMap.get(space);
        if (dataMap != null) {
            spaceMap.putAll(space, dataMap);
        }
        return spaceMap;
    }

    /** 所有Key集合 */
    public Set<T> valueSet() {
        Set<T> values = new HashSet<>();
        for (Map<K, T> mapItem : this.spaceMap.values()) {
            values.addAll(mapItem.values());
        }
        return values;
    }

    /** 命名空间下的key集合 */
    public Set<T> valueSet(String space) {
        Map<K, T> dataMap = this.spaceMap.get(space);
        if (dataMap != null) {
            return new HashSet<T>(dataMap.values());
        }
        return new HashSet<T>();
    }

    public Iterator<Map.Entry<K, T>> iterator() {
        Iterator<Map.Entry<K, T>> seqIterator = null;
        for (Map<K, T> mapItem : this.spaceMap.values()) {
            seqIterator = CollectionUtils.mergeIterator(seqIterator, mapItem.entrySet().iterator());
        }
        return seqIterator;
    }
}