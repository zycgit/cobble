/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.dynamic.beans;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2014-1-3
 */
public class GenericsMethodAopBean {
    public <T, V, Z> String fooCall1(T v1, V v2, Z v3) {
        return "abc";
    }

    public <T extends Date, V extends ArrayList> String fooCall2(T v1, V v2) {
        return "abc";
    }

    public String fooCall3(Class<? extends Date> v1, List<? extends Map<String, Date>> v2) {
        return "abc";
    }
}
