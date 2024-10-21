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
package net.hasor.cobble.reflect;
import net.hasor.cobble.BeanUtils;
import net.hasor.cobble.ClassUtils;
import net.hasor.cobble.CollectionUtils;
import net.hasor.cobble.StringUtils;
import net.hasor.cobble.convert.ConverterUtils;

import java.util.*;

/**
 * @author 赵永春 (zyc@hasor.net)
 */
public class Annotation {
    private final Map<String, List<Object>> confData;

    Annotation() {
        this.confData = new HashMap<>();
    }

    void putData(String name, String value) {
        this.confData.computeIfAbsent(name, s -> new ArrayList<>()).add(value);
    }

    void putData(String name, List<?> value) {
        this.confData.computeIfAbsent(name, s -> new ArrayList<>()).addAll(value);
    }

    void putData(String name, Annotation value) {
        this.confData.computeIfAbsent(name, s -> new ArrayList<>()).add(value);
    }

    public boolean getBoolean(String name) {
        return this.castValue(name, Boolean.TYPE);
    }

    public List<Boolean> getBooleanArray(String name) {
        return this.castValues(name, Boolean.TYPE);
    }

    public byte getByte(String name) {
        return this.castValue(name, Byte.TYPE);
    }

    public List<Byte> getByteArray(String name) {
        return this.castValues(name, Byte.TYPE);
    }

    public short getShort(String name) {
        return this.castValue(name, Short.TYPE);
    }

    public List<Short> getShortArray(String name) {
        return this.castValues(name, Short.TYPE);
    }

    public int getInt(String name) {
        return this.castValue(name, Integer.TYPE);
    }

    public List<Integer> getIntArray(String name) {
        return this.castValues(name, Integer.TYPE);
    }

    public long getLong(String name) {
        return this.castValue(name, Long.TYPE);
    }

    public List<Long> getLongArray(String name) {
        return this.castValues(name, Long.TYPE);
    }

    public float getFloat(String name) {
        return this.castValue(name, Float.TYPE);
    }

    public List<Float> getFloatArray(String name) {
        return this.castValues(name, Float.TYPE);
    }

    public double getDouble(String name) {
        return this.castValue(name, Double.TYPE);
    }

    public List<Double> getDoubleArray(String name) {
        return this.castValues(name, Double.TYPE);
    }

    public char getChar(String name) {
        return this.castValue(name, Character.class);
    }

    public List<Character> getCharArray(String name) {
        return this.castValues(name, Character.class);
    }

    public String getString(String name) {
        return this.castValue(name, String.class);
    }

    public List<String> getStringArray(String name) {
        return this.castValues(name, String.class);
    }

    public Class<?> getClass(String name) throws ClassNotFoundException {
        return this.castClass(name, null, false);
    }

    public Class<?> getClass(String name, boolean initialize) throws ClassNotFoundException {
        return this.castClass(name, null, initialize);
    }

    public Class<?> getClass(String name, ClassLoader classLoader) throws ClassNotFoundException {
        return this.castClass(name, classLoader, false);
    }

    public Class<?> getClass(String name, ClassLoader classLoader, boolean initialize) throws ClassNotFoundException {
        return this.castClass(name, classLoader, initialize);
    }

    public List<Class<?>> getClassArray(String name) throws ClassNotFoundException {
        return this.castClasses(name, null, false);
    }

    public List<Class<?>> getClassArray(String name, boolean initialize) throws ClassNotFoundException {
        return this.castClasses(name, null, initialize);
    }

    public List<Class<?>> getClassArray(String name, ClassLoader classLoader) throws ClassNotFoundException {
        return this.castClasses(name, classLoader, false);
    }

    public List<Class<?>> getClassArray(String name, ClassLoader classLoader, boolean initialize) throws ClassNotFoundException {
        return this.castClasses(name, classLoader, initialize);
    }

    public Annotation getAnnotation(String name) {
        return this.castAnnotation(name);
    }

    public Annotation getAnnotation(Class<?> name) {
        return this.castAnnotation(name.getName());
    }

    public List<Annotation> getAnnotationArray(String name) {
        return this.castAnnotations(name);
    }

    public List<Annotation> getAnnotationArray(Class<?> name) {
        return this.castAnnotations(name.getName());
    }

    protected <T> T castValue(String name, Class<?> toType) {
        Object data = this.confData.getOrDefault(name, null);
        if (data == null || ((List) data).isEmpty()) {
            if (toType.isPrimitive()) {
                return (T) BeanUtils.getDefaultValue(toType);
            } else {
                return null;
            }
        }

        return (T) ConverterUtils.convert(toType, ((List<?>) data).get(0));
    }

    protected <T> List<T> castValues(String name, Class<?> toType) {
        Object data = this.confData.getOrDefault(name, null);
        if (data == null || ((List) data).isEmpty()) {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>();
        for (Object o : ((List) data)) {
            if (o != null) {
                result.add((T) ConverterUtils.convert(toType, o));
            }
        }
        return Collections.unmodifiableList(result);
    }

    private Class<?> castClass(String name, ClassLoader classLoader, boolean initialize) throws ClassNotFoundException {
        String string = getString(name);
        if (StringUtils.isBlank(string)) {
            return null;
        } else {
            return ClassUtils.getClass(classLoader, string, initialize);
        }
    }

    private List<Class<?>> castClasses(String name, ClassLoader classLoader, boolean initialize) throws ClassNotFoundException {
        List<String> string = getStringArray(name);
        if (CollectionUtils.isEmpty(string)) {
            return null;
        } else {
            List<Class<?>> result = new ArrayList<>();
            for (String s : string) {
                result.add(ClassUtils.getClass(classLoader, s, initialize));
            }
            return Collections.unmodifiableList(result);
        }
    }

    private Annotation castAnnotation(String name) {
        Object data = this.confData.getOrDefault(name, null);
        if (data == null || ((List) data).isEmpty()) {
            return null;
        }

        Object obj = ((List<?>) data).get(0);
        if (obj instanceof Annotation) {
            return (Annotation) obj;
        } else {
            return null;
        }
    }

    private List<Annotation> castAnnotations(String name) {
        Object data = this.confData.getOrDefault(name, null);
        if (data == null || ((List) data).isEmpty()) {
            return Collections.emptyList();
        }

        List<Annotation> result = new ArrayList<>();
        for (Object o : ((List) data)) {
            if (o instanceof Annotation) {
                result.add((Annotation) o);
            }
        }
        return Collections.unmodifiableList(result);
    }

    boolean isEmpty() {
        return this.confData.isEmpty();
    }
}
