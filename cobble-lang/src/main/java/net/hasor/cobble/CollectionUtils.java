/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.hasor.cobble.function.Property;
import net.hasor.cobble.ref.BufferedIterator;
import net.hasor.cobble.reflect.SFunction;

/**
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2013-4-12
 */
public class CollectionUtils {
    // Empty utilities
    //--------------------------------------------------------------------------
    public static boolean isNotEmpty(Collection<?> tables) {
        return tables != null && !tables.isEmpty();
    }

    public static boolean isEmpty(Collection<?> tables) {
        return tables == null || tables.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> maps) {
        return maps != null && !maps.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> maps) {
        return maps == null || maps.isEmpty();
    }

    public static int size(Collection<?> list) {
        return (list == null || list.isEmpty()) ? 0 : list.size();
    }

    public static int size(Map<?, ?> map) {
        return (map == null || map.isEmpty()) ? 0 : map.size();
    }
    // split utilities
    //--------------------------------------------------------------------------

    /**
     * 切分list
     * @param groupSize 每组定长
     */
    public static <T> List<List<T>> splitList(List<T> sourceList, int groupSize) {
        groupSize = Math.max(1, groupSize);
        int length = sourceList.size();
        // 计算可以分成多少组
        long num = (length + (long) groupSize - 1) / groupSize;

        List<List<T>> newList;
        if (groupSize > 8192) {
            newList = new LinkedList<>();
        } else {
            newList = new ArrayList<>((int) (groupSize * 0.25));
        }

        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = Math.min((i + 1) * groupSize, length);
            newList.add(sourceList.subList(fromIndex, toIndex));
        }
        return newList;
    }

    // Iterator/Enumeration utilities
    //--------------------------------------------------------------------------
    public static <T> BufferedIterator<T> bufferedIterator(final Iterator<T> oriIterator, final int bufferedSize) {
        return new BufferedIterator<>(oriIterator, bufferedSize);
    }

    /** 迭代器类型转换 */
    public static <T, O> Iterator<O> convertIterator(final Iterator<T> oriIterator, final Function<T, O> converter) {
        return new Iterator<O>() {
            @Override
            public void remove() {
                oriIterator.remove();
            }

            @Override
            public O next() {
                return converter.apply(oriIterator.next());
            }

            @Override
            public boolean hasNext() {
                return oriIterator.hasNext();
            }
        };
    }

    /** 转换为 Enumeration */
    public static <T> Enumeration<T> asEnumeration(final List<T> list) {
        return list == null ? null : asEnumeration(list.iterator());
    }

    /** 转换为 Enumeration */
    public static <T> Enumeration<T> asEnumeration(final Iterator<T> iterator) {
        return new Enumeration<T>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public T nextElement() {
                return iterator.next();
            }
        };
    }

    public static <T> List<T> asList(Enumeration<T> enumeration) {
        if (enumeration == null) {
            return Collections.emptyList();
        }
        ArrayList<T> arrayList = new ArrayList<>();
        while (enumeration.hasMoreElements()) {
            arrayList.add(enumeration.nextElement());
        }
        return arrayList;
    }

    public static <T> List<T> asList(T... arrays) {
        if (arrays == null || arrays.length == 0) {
            return new ArrayList<>();
        }
        ArrayList<T> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, arrays);
        return arrayList;
    }

    public static <T> List<T> asList(Iterator<T> iterator) {
        if (iterator == null) {
            return Collections.emptyList();
        }
        ArrayList<T> arrayList = new ArrayList<>();
        while (iterator.hasNext()) {
            arrayList.add(iterator.next());
        }
        return arrayList;
    }

    public static <K, V> Map<K, V> asMap(K key1, V val1) {
        return asMap(key1, val1, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public static <K, V> Map<K, V> asMap(K key1, V val1, K key2, V val2) {
        return asMap(key1, val1, key2, val2, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public static <K, V> Map<K, V> asMap(K key1, V val1, K key2, V val2, K key3, V val3) {
        return asMap(key1, val1, key2, val2, key3, val3, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public static <K, V> Map<K, V> asMap(K key1, V val1, K key2, V val2, K key3, V val3, K key4, V val4) {
        return asMap(key1, val1, key2, val2, key3, val3, key4, val4, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public static <K, V> Map<K, V> asMap(K key1, V val1, K key2, V val2, K key3, V val3, K key4, V val4, K key5, V val5) {
        return asMap(key1, val1, key2, val2, key3, val3, key4, val4, key5, val5, null, null, null, null, null, null, null, null, null, null);
    }

    public static <K, V> Map<K, V> asMap(K key1, V val1, K key2, V val2, K key3, V val3, K key4, V val4, K key5, V val5, K key6, V val6) {
        return asMap(key1, val1, key2, val2, key3, val3, key4, val4, key5, val5, key6, val6, null, null, null, null, null, null, null, null);
    }

    public static <K, V> Map<K, V> asMap(K key1, V val1, K key2, V val2, K key3, V val3, K key4, V val4, K key5, V val5, K key6, V val6, K key7, V val7) {
        return asMap(key1, val1, key2, val2, key3, val3, key4, val4, key5, val5, key6, val6, key7, val7, null, null, null, null, null, null);
    }

    public static <K, V> Map<K, V> asMap(K key1, V val1, K key2, V val2, K key3, V val3, K key4, V val4, K key5, V val5, K key6, V val6, K key7, V val7, K key8, V val8) {
        return asMap(key1, val1, key2, val2, key3, val3, key4, val4, key5, val5, key6, val6, key7, val7, key8, val8, null, null, null, null);
    }

    public static <K, V> Map<K, V> asMap(K key1, V val1, K key2, V val2, K key3, V val3, K key4, V val4, K key5, V val5, K key6, V val6, K key7, V val7, K key8, V val8, K key9, V val9) {
        return asMap(key1, val1, key2, val2, key3, val3, key4, val4, key5, val5, key6, val6, key7, val7, key8, val8, key9, val9, null, null);
    }

    public static <K, V> Map<K, V> asMap(K key1, V val1, K key2, V val2, K key3, V val3, K key4, V val4, K key5, V val5, K key6, V val6, K key7, V val7, K key8, V val8, K key9, V val9, K key0, V val0) {
        Map<K, V> map = new HashMap<>();
        if (key1 != null) {
            map.put(key1, val1);
        }
        if (key2 != null) {
            map.put(key2, val2);
        }
        if (key3 != null) {
            map.put(key3, val3);
        }
        if (key4 != null) {
            map.put(key4, val4);
        }
        if (key5 != null) {
            map.put(key5, val5);
        }
        if (key6 != null) {
            map.put(key6, val6);
        }
        if (key7 != null) {
            map.put(key7, val7);
        }
        if (key8 != null) {
            map.put(key8, val8);
        }
        if (key9 != null) {
            map.put(key9, val9);
        }
        if (key0 != null) {
            map.put(key0, val0);
        }
        return map;
    }

    //--------------------------------------------------------------------------

    /** 合并两个迭代器 */
    public static <T> Enumeration<T> mergeEnumeration(final Enumeration<T> enum1, final Enumeration<T> enum2) {
        final Enumeration<T> i1 = enum1 != null ? enum1 : CollectionUtils.asEnumeration(Collections.emptyIterator());
        final Enumeration<T> i2 = enum2 != null ? enum2 : CollectionUtils.asEnumeration(Collections.emptyIterator());
        return new Enumeration<T>() {
            private Enumeration<T> it = i1;

            @Override
            public boolean hasMoreElements() {
                return i1.hasMoreElements() || i2.hasMoreElements();
            }

            @Override
            public T nextElement() {
                if (!this.it.hasMoreElements()) {
                    this.it = i2;
                }
                return this.it.nextElement();
            }
        };
    }

    /** 合并两个迭代器 */
    public static <T> Iterator<T> mergeIterator(final Iterator<T> iterator1, final Iterator<T> iterator2) {
        final Iterator<T> i1 = iterator1 != null ? iterator1 : Collections.emptyIterator();
        final Iterator<T> i2 = iterator2 != null ? iterator2 : Collections.emptyIterator();
        return new Iterator<T>() {
            private Iterator<T> it = i1;

            @Override
            public boolean hasNext() {
                return i1.hasNext() || i2.hasNext();
            }

            @Override
            public T next() {
                if (!this.it.hasNext()) {
                    this.it = i2;
                }
                return this.it.next();
            }

            @Override
            public void remove() {
                this.it.remove();
            }
        };
    }

    public static boolean isEmpty(Object[] args) {
        return args == null || args.length == 0;
    }

    /**
     * Returns <code>true</code> iff at least one element is in both collections.
     * <p>
     * In other words, this method returns <code>true</code> iff the
     * intersection of <i>coll1</i> and <i>coll2</i> is not empty.
     * @param coll1 the first collection, must not be null
     * @param coll2 the first collection, must not be null
     * @return <code>true</code> iff the intersection of the collections is non-empty
     * @since 2.1
     */
    public static boolean containsAny(final Collection<?> coll1, final Collection<?> coll2) {
        if (coll1.size() < coll2.size()) {
            for (Object o : coll1) {
                if (coll2.contains(o)) {
                    return true;
                }
            }
        } else {
            for (Object o : coll2) {
                if (coll1.contains(o)) {
                    return true;
                }
            }
        }
        return false;
    }
    //--------------------------------------------------------------------------

    /**
     * 将 dataList 转换成 Tree 结构。
     * <li>1. 并且接收结果集的对象必须含有 id, pid, children 三个属性</li>
     * <li>2. 如果 PID 在结果集的 ID 集中不存在，也会成为一个根节点</li>
     * <li>3. 用于 Tree 查询的结果集中必须含有下列数据结构</li>
     * <br/><pre>
     * | ID | PID  | NAME       | ... |
     * | 1  | NULL | TreeNode 1 | ... |
     * | 2  | 1    | TreeNode 2 | ... |
     * | 3  | 2    | TreeNode 3 | ... |
     * | 4  | 1    | TreeNode 4 | ... |
     * | 5  | NULL | TreeNode 5 | ... |
     * </pre>
     * <li>结果顺序及 Tree nodes 顺序与查询结果保持不变</li>
     * @param treeID Tree 的 ID 属性
     * @param parent Tree 结构中父节点属性
     * @param children Tree 子节点列表
     */
    public static <T> List<T> toTree(List<T> dataList, SFunction<T> children, SFunction<T> treeID, SFunction<T> parent) {
        if (dataList.isEmpty()) {
            return dataList;
        }
        Class<?> type = dataList.get(0).getClass();
        Property childrenField = BeanUtils.getPropertyFunc(type, BeanUtils.toProperty(children));
        Property treeIdField = BeanUtils.getPropertyFunc(type, BeanUtils.toProperty(treeID));
        Property parentField = BeanUtils.getPropertyFunc(type, BeanUtils.toProperty(parent));
        return toPidTree(dataList, childrenField, treeIdField, parentField, o -> o);
    }

    /**
     * 将 dataList 转换成 Tree 结构。
     * <li>1. 并且接收结果集的对象必须含有 id, pid, children 三个属性</li>
     * <li>2. 如果 PID 在结果集的 ID 集中不存在，也会成为一个根节点</li>
     * <li>3. 用于 Tree 查询的结果集中必须含有下列数据结构</li>
     * <br/><pre>
     * | ID | PID  | NAME       | ... |
     * | 1  | NULL | TreeNode 1 | ... |
     * | 2  | 1    | TreeNode 2 | ... |
     * | 3  | 2    | TreeNode 3 | ... |
     * | 4  | 1    | TreeNode 4 | ... |
     * | 5  | NULL | TreeNode 5 | ... |
     * </pre>
     * <li>结果顺序及 Tree nodes 顺序与查询结果保持不变</li>
     * @param treeID Tree 的 ID 属性
     * @param parent Tree 结构中父节点属性
     * @param children Tree 子节点列表
     */
    public static List<Map<String, Object>> toTree(List<Map<String, Object>> dataList, String children, String treeID, String parent) {
        if (dataList.isEmpty()) {
            return dataList;
        }
        Property childrenField = BeanUtils.getPropertyFunc(Map.class, children);
        Property treeIdField = BeanUtils.getPropertyFunc(Map.class, treeID);
        Property parentField = BeanUtils.getPropertyFunc(Map.class, parent);
        return toPidTree(dataList, childrenField, treeIdField, parentField, o -> o);
    }

    /**
     * 将 dataList 转换成 Tree 结构。
     * 并将 pathField 字段值作为路径 Key；将结果集按照前 路径 缀匹配方式转换为 Tree 结构。
     * <br/><pre>
     * | ID | PATH | NAME       | ... |
     * | 1  | NULL | TreeNode 1 | ... |
     * | 2  | /a   | TreeNode 2 | ... |
     * | 3  | /a/b | TreeNode 3 | ... |
     * | 4  | /b   | TreeNode 4 | ... |
     * | 5  | /b/c | TreeNode 5 | ... |
     * </pre>
     * <li>结果顺序及 Tree nodes 顺序与查询结果保持不变</li>
     * @param pathCode Tree 的 PATH 属性
     * @param children Tree 子节点列表
     */
    public static <T> List<T> toTree(List<T> dataList, SFunction<T> children, SFunction<T> pathCode) {
        if (dataList.isEmpty()) {
            return dataList;
        }
        Class<?> type = dataList.get(0).getClass();
        Property childrenField = BeanUtils.getPropertyFunc(type, BeanUtils.toProperty(children));
        Property pathCodeField = BeanUtils.getPropertyFunc(type, BeanUtils.toProperty(pathCode));
        return toPathTree(dataList, childrenField, pathCodeField, o -> o);
    }

    /**
     * 将 dataList 转换成 Tree 结构。
     * 并将 pathField 字段值作为路径 Key；将结果集按照前 路径 缀匹配方式转换为 Tree 结构。
     * <br/><pre>
     * | ID | PATH | NAME       | ... |
     * | 1  | NULL | TreeNode 1 | ... |
     * | 2  | /a   | TreeNode 2 | ... |
     * | 3  | /a/b | TreeNode 3 | ... |
     * | 4  | /b   | TreeNode 4 | ... |
     * | 5  | /b/c | TreeNode 5 | ... |
     * </pre>
     * <li>结果顺序及 Tree nodes 顺序与查询结果保持不变</li>
     * @param pathCode Tree 的 PATH 属性
     * @param children Tree 子节点列表
     */
    public static List<Map<String, Object>> toTree(List<Map<String, Object>> dataList, String children, String pathCode) {
        if (dataList.isEmpty()) {
            return dataList;
        }
        Property childrenField = BeanUtils.getPropertyFunc(Map.class, children);
        Property pathCodeField = BeanUtils.getPropertyFunc(Map.class, pathCode);
        return toPathTree(dataList, childrenField, pathCodeField, o -> o);
    }

    private static <I, O> List<O> toPidTree(List<I> dataList, Property childrenField, //
            Property treeIdField, Property parentField, Function<I, O> convert) {
        // init property of [treeID\parent\children]
        if (treeIdField == null || parentField == null || childrenField == null || childrenField.isReadOnly()) {
            throw new IllegalArgumentException("[treeID, parent, children] not exist or children is readOnly.");
        }

        // init treeList (use LinkedHashMap)
        Map<Object, I> dataMap = new LinkedHashMap<>();
        for (I node : dataList) {
            Object tid = treeIdField.get(node);
            if (tid == null) {
                throw new IllegalArgumentException(" treeID is null.");
            } else {
                dataMap.put(tid, node);
            }
        }

        // extract tree structure
        Set<Object> needRemove = new HashSet<>();
        for (I node : dataList) {
            Object tid = treeIdField.get(node);
            Object parentId = parentField.get(node);
            if (parentId == null || !dataMap.containsKey(parentId)) {
                continue;
            }

            // get or init children list
            Object parentData = dataMap.get(parentId);
            List childrenList = (List) childrenField.get(parentData);
            if (childrenList == null) {
                childrenList = new ArrayList<>();
                childrenField.set(parentData, childrenList);
            }

            // move node to children
            childrenList.add(node);
            needRemove.add(tid);
        }

        for (Object remove : needRemove) {
            dataMap.remove(remove);
        }
        return dataMap.values().stream().map(convert).collect(Collectors.toList());
    }

    private static <I, O> List<O> toPathTree(List<I> dataList, Property childrenField, //
            Property pathCodeField, Function<I, O> convert) {
        // init property of [pathCode\children]
        if (pathCodeField == null || childrenField == null || childrenField.isReadOnly()) {
            throw new IllegalArgumentException("[pathCode, children] not exist or children is readOnly.");
        }

        // data
        List<I> sortedList = dataList.stream().sorted((o1, o2) -> {
            Object o1Path = pathCodeField.get(o1);
            Object o2Path = pathCodeField.get(o2);
            String o1PathStr = o1Path == null ? "" : o1Path.toString();
            String o2PathStr = o2Path == null ? "" : o2Path.toString();
            return o1PathStr.compareTo(o2PathStr);
        }).collect(Collectors.toList());

        // init children list
        List<String> allPathList = new ArrayList<>();
        Map<String, List<I>> allPathMap = new HashMap<>();

        for (I item : sortedList) {
            Object path = pathCodeField.get(item);
            String pathStr = (path == null) ? null : path.toString();
            if (StringUtils.isBlank(pathStr)) {
                allPathMap.computeIfAbsent(pathStr, k -> new ArrayList<>()).add(item);
                continue;
            }

            String parentPath = null;//parent path item
            for (String pItem : allPathList) {
                if (StringUtils.startsWith(pathStr, pItem) && !StringUtils.equals(pathStr, pItem)) {
                    parentPath = pItem;
                    break;
                }
            }

            if (parentPath != null) {
                List<I> parentList = allPathMap.get(parentPath);
                I parent = parentList.get(0);
                List childrenList = (List) childrenField.get(parent);
                if (childrenList == null) {
                    childrenList = new ArrayList<>();
                    childrenField.set(parent, childrenList);
                }
                insertOrAppend(dataList, childrenList, item);
            } else if (allPathMap.containsKey(pathStr)) {
                List<I> list = allPathMap.get(pathStr);
                list.add(item);
            } else {
                allPathList.add(0, pathStr);
                List<I> list = new ArrayList<>();
                list.add(item);
                allPathMap.put(pathStr, list);
            }
        }

        // extract tree structure
        List<O> result = new ArrayList<>();
        for (List<I> nodeList : allPathMap.values()) {
            for (I node : nodeList) {
                insertOrAppend(dataList, result, convert.apply(node));
            }
        }
        return result;
    }

    private static void insertOrAppend(List oriData, List waitInsert, Object item) {
        int pos = waitInsert.size();

        int pubPos = oriData.indexOf(item);
        for (Object obj : waitInsert) {
            int objPubPos = oriData.indexOf(obj);
            if (pubPos < objPubPos) {
                int newPos = waitInsert.indexOf(obj);
                if (newPos < pos) {
                    pos = newPos;
                }
            }
        }

        waitInsert.add(pos, item);
    }

    /** 将一个 List 按照 groupBy 函数进行分组 */
    public static <T, G> Map<G, List<T>> groupBy(List<T> list, Function<T, G> groupBy) {
        return groupBy(list, groupBy, t -> t);
    }

    /** 将一个 List 按照 groupBy 函数进行分组并使用 convert 函数进行转换 */
    public static <T, G, V> Map<G, List<V>> groupBy(List<T> list, Function<T, G> groupBy, Function<T, V> convert) {
        if (list == null) {
            return Collections.emptyMap();
        }
        Map<G, List<V>> groupMap = new HashMap<>();
        for (T t : list) {
            groupMap.computeIfAbsent(groupBy.apply(t), k -> new ArrayList<>()).add(convert.apply(t));
        }
        return groupMap;
    }
}
