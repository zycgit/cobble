/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import net.hasor.cobble.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 元组。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2016-07-17
 */
public class Tuple {
    private final List<Object> values;

    public Tuple(Object... values) {
        this.values = new ArrayList<>(values.length);
        Collections.addAll(this.values, values);
    }

    public static Tuple of(Object... values) {
        return new Tuple(values);
    }

    public Object[] toArray() {
        return this.values.toArray();
    }

    public List<Object> toList() {
        return this.values;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Tuple)) {
            return false;
        }
        Tuple other = (Tuple) obj;
        return Arrays.deepEquals(this.values.toArray(), other.values.toArray());
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.values.toArray());
    }

    @Override
    public String toString() {
        return "Tuple[" + StringUtils.join(this.values, ", ") + "]";
    }
}