/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

/***
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class CollectionUtilsTest {
    @Test
    public void splitList_01() {
        List<String> data = Arrays.asList("1", "2", "3", "4", "5", "6");
        assert CollectionUtils.splitList(data, Integer.MAX_VALUE).size() == 1;
        assert CollectionUtils.splitList(data, Integer.MIN_VALUE).size() == data.size();
        assert CollectionUtils.splitList(data, 1).size() == data.size();
        assert CollectionUtils.splitList(data, 2).size() == 3;
        assert CollectionUtils.splitList(data, 3).size() == 2;
        assert CollectionUtils.splitList(data, 4).size() == 2;
        assert CollectionUtils.splitList(data, 5).size() == 2;
        assert CollectionUtils.splitList(data, 6).size() == 1;
    }
}
