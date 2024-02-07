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
package net.hasor.cobble;
import net.hasor.cobble.convert.ConverterUtils;
import net.hasor.cobble.function.Property;
import net.hasor.cobble.reflect.MethodUtils;
import net.hasor.cobble.reflect.SFunction;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;

/**
 *
 * @version : 2011-6-3
 * @author 赵永春 (zyc@hasor.net)
 */
public class BeanUtils {
    private static final Annotation[] ANNOTATION_EMPTY = new Annotation[0];

    /**获取指定类型的默认值 */
    public static Object getDefaultValue(final Class<?> returnType) {
        if (returnType == null || !returnType.isPrimitive()) {
            return null;
        }
        //
        if (returnType == int.class) {
            return 0;
        } else if (returnType == byte.class) {
            return (byte) 0;
        } else if (returnType == char.class) {
            return '\0';
        } else if (returnType == double.class) {
            return 0d;
        } else if (returnType == float.class) {
            return 0f;
        } else if (returnType == long.class) {
            return 0L;
        } else if (returnType == short.class) {
            return (short) 0;
        } else if (returnType == boolean.class) {
            return false;
        } else if (returnType == void.class) {
            return null;
        } else if (returnType.isArray()) {
            return null;
        }
        return null;
    }

    public static Object[] getDefaultValues(Class<?>[] paramArray) {
        if (paramArray == null) {
            return null;
        }
        Object[] objs = new Object[paramArray.length];
        for (int i = 0; i < paramArray.length; i++) {
            objs[i] = getDefaultValue(paramArray[i]);
        }
        return objs;
    }

    /** 查找一个可操作的字段列表 */
    public static List<Field> getFields(final Class<?> type) {
        return Arrays.asList(type.getFields());
    }

    /** 查找字段 */
    public static Map<String, Field> getFieldMap(final Class<?> target, final String field) {
        if (field == null || target == null) {
            return null;
        }
        Map<String, Field> result = new LinkedHashMap<>();
        for (Field f : target.getFields()) {
            result.put(f.getName(), f);
        }
        return result;
    }

    /** 查找一个可操作的方法列表 */
    public static List<Method> getMethods(final Class<?> type) {
        return Arrays.asList(type.getMethods());
    }

    /** 查找一个可操作的字段 */
    public static Field getField(final Class<?> target, final String field) {
        if (field == null || target == null) {
            return null;
        }
        for (Field f : target.getFields()) {
            if (f.getName().equals(field)) {
                return f;
            }
        }
        return null;
    }

    /** 查找一个可操作的方法 */
    public static Method getMethod(final Class<?> target, final String name, final Class<?>[] paramType) {
        try {
            return target.getMethod(name, paramType);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 该方法的作用是反射的形式调用目标的方法。
     * @param target 被调用的对象
     * @param name 要调用的反射方法名。
     * @param objects 参数列表
     */
    public static Object invokeMethod(final Object target, final String name, final Object... objects) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (target == null) {
            return null;
        }
        Class<?> targetType = target.getClass();
        Method invokeMethod = null;
        //反射调用方法
        Method[] ms = targetType.getMethods();
        for (Method m : ms) {
            //1.名字不相等的忽略
            if (!m.getName().equals(name)) {
                continue;
            }
            //2.目标方法参数列表个数与types字段中存放的个数不一样的忽略。
            Class<?>[] paramTypes = m.getParameterTypes();
            if (paramTypes.length != objects.length) {
                continue;
            }
            //3.如果有参数类型不一样的也忽略---1
            boolean isFind = true;
            for (int i = 0; i < paramTypes.length; i++) {
                Object param_object = objects[i];
                if (param_object == null) {
                    continue;
                }
                //
                if (!paramTypes[i].isAssignableFrom(param_object.getClass())) {
                    isFind = false;
                    break;
                }
            }
            //5.如果有参数类型不一样的也忽略---2
            if (!isFind) {
                continue;
            }
            //符合条件执行调用
            invokeMethod = m;
        }
        if (invokeMethod == null) {
            throw new NullPointerException(name + " invokeMethod is null.");
        } else {
            return invokeMethod.invoke(target, objects);
        }
    }
    /*----------------------------------------------------------------------------------------*/

    /** 获取类定义的字段和继承父类中定义的字段以及父类的父类（子类重新定义同名字段也会被列入集合） */
    public static List<Field> getALLFieldToList(final Class<?> target) {
        if (target == null) {
            return null;
        }
        ArrayList<Field> fList = new ArrayList<>();
        findALLFields(target, fList);
        return fList;
    }

    private static void findALLFields(final Class<?> target, final List<Field> fList) {
        if (target == null) {
            return;
        }
        for (Field field : target.getDeclaredFields()) {
            if (!fList.contains(field)) {
                fList.add(field);
            }
        }
        for (Field field : target.getFields()) {
            if (!fList.contains(field)) {
                fList.add(field);
            }
        }
        Class<?> superType = target.getSuperclass();
        if (superType == null || superType == target) {
            return;
        }
        findALLFields(superType, fList);
    }

    /** 获取类定义的字段和继承父类中定义的字段以及父类的父类（子类重新定义同名字段也会被列入集合） */
    public static Map<String, Field> getALLFieldToMap(final Class<?> target) {
        if (target == null) {
            return null;
        }

        Map<String, Field> fMap = new LinkedHashMap<>();
        findALLFields(target, fMap);
        return fMap;
    }

    private static void findALLFields(final Class<?> target, final Map<String, Field> fMap) {
        if (target == null) {
            return;
        }

        for (Field f : target.getDeclaredFields()) {
            if (!fMap.containsKey(f.getName())) {
                fMap.put(f.getName(), f);
            }
        }
        for (Field f : target.getFields()) {
            if (!fMap.containsKey(f.getName())) {
                fMap.put(f.getName(), f);
            }
        }

        Class<?> superType = target.getSuperclass();
        if (superType == null || superType == target) {
            return;
        }
        findALLFields(superType, fMap);
    }

    /** 获取类定义的方法和继承父类中定义的方法以及父类的父类（子类的重写方法也会被返回） */
    public static List<Method> getAllMethodToList(final Class<?> target) {
        if (target == null) {
            return null;
        }
        ArrayList<Method> mList = new ArrayList<>();
        findALLMethods(target, mList);
        return mList;
    }

    private static void findALLMethods(final Class<?> target, final ArrayList<Method> mList) {
        if (target == null) {
            return;
        }
        for (Method method : target.getDeclaredMethods()) {
            if (!mList.contains(method)) {
                mList.add(method);
            }
        }
        for (Method method : target.getMethods()) {
            if (!mList.contains(method)) {
                mList.add(method);
            }
        }
        Class<?> superType = target.getSuperclass();
        if (superType == null || superType == target) {
            return;
        }
        findALLMethods(superType, mList);
    }

    /*----------------------------------------------------------------------------------------*/

    /** 获取属性名集合，被包含的属性可能有些只是只读属性，有些是只写属性。也有读写属性 */
    public static List<String> getProperties(final Class<?> target) {
        List<String> mnames = new ArrayList<>();
        List<Method> ms = getMethods(target);
        for (Method m : ms) {
            if (m.getDeclaringClass() == Object.class) {
                continue;
            }
            String name = m.getName();
            if (name.startsWith("get") || name.startsWith("set")) {
                name = name.substring(3);
            } else if (name.startsWith("is")) {
                name = name.substring(2);
            } else {
                continue;
            }
            if (!name.equals("")) {
                name = StringUtils.firstCharToLowerCase(name);
                if (!mnames.contains(name)) {
                    mnames.add(name);
                }
            }
        }
        return mnames;
    }

    /** 获取属性名集合，被包含的属性可能有些只是只读属性，有些是只写属性。也有读写属性 */
    public static Annotation[] getPropertyAnnotation(final Class<?> target, final String property) {
        Map<String, Property> propertyMap = getPropertyFunc(target, test -> test.getName().equals(property));
        if (!propertyMap.containsKey(property)) {
            return ANNOTATION_EMPTY;
        }

        return getPropertyAnnotation(propertyMap.get(property));
    }

    /** 获取属性名集合，被包含的属性可能有些只是只读属性，有些是只写属性。也有读写属性 */
    public static Annotation[] getPropertyAnnotation(final Property property) {
        if (!(property instanceof SimpleProperty)) {
            return ANNOTATION_EMPTY;
        }

        SimpleProperty propertyFunc = (SimpleProperty) property;
        AccessibleObject propertyField = propertyFunc.getField();
        AccessibleObject readerHandler = propertyFunc.getReaderHandler();
        AccessibleObject writerHandler = propertyFunc.getWriterHandler();

        Annotation[] arrays1 = readerHandler == null ? ANNOTATION_EMPTY : propertyField.getAnnotations();
        Annotation[] arrays2 = readerHandler == null ? ANNOTATION_EMPTY : readerHandler.getAnnotations();
        Annotation[] arrays3 = writerHandler == null ? ANNOTATION_EMPTY : writerHandler.getAnnotations();

        Annotation[] result = new Annotation[arrays1.length + arrays2.length + arrays3.length];

        System.arraycopy(arrays1, 0, result, 0, arrays1.length);
        System.arraycopy(arrays2, 0, result, arrays1.length, arrays2.length);
        System.arraycopy(arrays3, 0, result, arrays1.length + arrays2.length, arrays3.length);
        return result;
    }

    /** 获取属性名集合，被包含的属性可能有些只是只读属性，有些是只写属性。也有读写属性 */
    public static Map<String, Annotation[]> getPropertyAnnotation(final Class<?> target) {
        Map<String, Annotation[]> result = new LinkedHashMap<>();

        Map<String, Property> propertyMap = getPropertyFunc(target, test -> true);
        propertyMap.forEach((name, property) -> {
            result.put(name, getPropertyAnnotation(property));
        });

        return result;
    }

    /** 获取属性名集合，被包含的属性可能有些只是只读属性，有些是只写属性。也有读写属性 */
    public static PropertyDescriptor[] getPropertyDescriptors(final Class<?> defineType) {
        List<PropertyDescriptor> mnames = new ArrayList<>();
        List<String> ms = getProperties(defineType);
        for (String m : ms) {
            try {
                mnames.add(new PropertyDescriptor(m, defineType));
            } catch (Exception e) {
            }
        }
        return mnames.toArray(new PropertyDescriptor[0]);
    }

    /** 获取一个属性的读取方法 */
    public static Method getReadMethod(final Class<?> target, final String property) {
        if (property == null || target == null) {
            return null;
        }
        String methodName_1 = "get" + StringUtils.firstCharToUpperCase(property);
        String methodName_2 = "is" + StringUtils.firstCharToUpperCase(property);
        //
        for (Method m : target.getMethods()) {
            if (m.getParameterTypes().length == 0) {
                String methodName = m.getName();
                if (methodName.equals(methodName_1)) {
                    return m;
                }
                /*是否是布尔*/
                if (methodName.equals(methodName_2)) {
                    Class<?> t = m.getReturnType();
                    if (t == Boolean.class || t == boolean.class) {
                        return m;
                    }
                }
            }
        }
        return null;
    }

    /** 获取一个属性的写入方法 */
    public static Method getWriteMethod(final Class<?> target, final String property) {
        if (property == null || target == null) {
            return null;
        }
        String methodName = "set" + StringUtils.firstCharToUpperCase(property);
        for (Method m : target.getMethods()) {
            if (m.getName().equals(methodName)) {
                if (m.getParameterTypes().length == 1) {
                    return m;
                }
            }
        }
        return null;
    }

    /** 获取属性名集合，被包含的属性可能有些只是只读属性，有些是只写属性。也有读写属性 */
    public static Property getPropertyFunc(final Class<?> target, final String property) {
        if (Map.class.isAssignableFrom(target)) {
            return new MapProperty(property);
        } else {
            return getPropertyFunc(target).get(property);
        }
    }

    public static Property createMapPropertyFunc(final String property) {
        if (StringUtils.isBlank(property)) {
            throw new IllegalArgumentException("property isBlank.");
        } else {
            return new MapProperty(property);
        }
    }

    /** 获取属性名集合，被包含的属性可能有些只是只读属性，有些是只写属性。也有读写属性 */
    public static Map<String, Property> getPropertyFunc(final Class<?> target) {
        return getPropertyFunc(target, test -> true);
    }

    private static Map<String, Property> getPropertyFunc(final Class<?> target, Predicate<Method> test) {
        Map<String, Property> propertyMap = new LinkedHashMap<>();
        Map<String, Field> allFields = getALLFieldToMap(target);

        for (Method m : target.getMethods()) {
            if (m.getDeclaringClass() == Object.class || !test.test(m)) {
                continue;
            }

            String name = m.getName();
            if (name.startsWith("get")) {
                name = StringUtils.firstCharToLowerCase(name.substring(3));
                ((SimpleProperty) propertyMap.computeIfAbsent(name, s -> new SimpleProperty())).setReader(m);
            } else if (name.startsWith("is")) {
                name = StringUtils.firstCharToLowerCase(name.substring(2));
                ((SimpleProperty) propertyMap.computeIfAbsent(name, s -> new SimpleProperty())).setReader(m);
            } else if (name.startsWith("set")) {
                name = StringUtils.firstCharToLowerCase(name.substring(3));
                ((SimpleProperty) propertyMap.computeIfAbsent(name, s -> new SimpleProperty())).setWriter(m);
            }

            Field field = allFields.get(name);
            if (field != null) {
                ((SimpleProperty) propertyMap.computeIfAbsent(name, s -> new SimpleProperty())).setField(field);
            }
        }

        return propertyMap;
    }

    /** 获取属性名集合，被包含的属性可能有些只是只读属性，有些是只写属性。也有读写属性 */
    public static Property getFieldFunc(final Class<?> target, final String property) {
        Map<String, Property> fieldFunc = getFieldFunc(target, test -> test.getName().equals(property));
        return fieldFunc.get(property);
    }

    /** 获取属性名集合，被包含的属性可能有些只是只读属性，有些是只写属性。也有读写属性 */
    public static Map<String, Property> getFieldFunc(final Class<?> target) {
        return getFieldFunc(target, test -> true);
    }

    private static Map<String, Property> getFieldFunc(final Class<?> target, Predicate<Field> test) {
        Map<String, Property> propertyMap = new LinkedHashMap<>();
        for (Field f : target.getFields()) {
            if (!test.test(f)) {
                continue;
            }
            ((SimpleProperty) propertyMap.computeIfAbsent(f.getName(), s -> new SimpleProperty())).setField(f);
        }
        return propertyMap;
    }

    /** 测试是否支持 readProperty 方法, 返回 true 表示可以进行读取操作 */
    public static boolean canReadProperty(final Class<?> target, final String property) {
        Method readMethod = getReadMethod(target, property);
        return readMethod != null;
    }

    /** 测试是否支持 writeProperty 方法, 返回 true 表示可以进行写入操作 */
    public static boolean canWriteProperty(final Class<?> target, final String property) {
        Method writeMethod = getWriteMethod(target, property);
        return writeMethod != null;
    }

    /** 测试是否支持 Field 方法写 */
    public static boolean canWriteField(final Class<?> target, final String property) {
        Field field = getField(target, property);
        return field != null && !Modifier.isFinal(field.getModifiers());
    }

    /** 测试是否支持 Field 方法读 */
    public static boolean canReadField(final Class<?> target, final String property) {
        return true;
    }

    /** 测试是否具有 property 所表示的属性, 无论是读或写方法只要存在一个就表示存在该属性 */
    public static boolean hasProperty(final Class<?> target, final String property) {
        if (getReadMethod(target, property) == null) {
            return getWriteMethod(target, property) != null;
        }
        return true;
    }

    /** 测试是否具有 fieldName 所表示的字段, 无论是读或写方法只要存在一个就表示存在该属性 */
    public static boolean hasField(final Class<?> target, final String property) {
        return getField(target, property) != null;
    }

    /**执行属性注入，除了注入int,short,long,等基本类型之外该方法还支持注入枚举类型。返回值表示执行是否成功。注意：该方法会根据属性类型进行尝试类型转换 */
    public static boolean writeProperty(final Object object, final String property, final Object value) {
        if (object == null || property == null) {
            return false;
        }
        //1.查找方法
        Class<?> defineType = object.getClass();
        Method writeMethod = getWriteMethod(defineType, property);
        if (writeMethod == null) {
            return false;
        }
        //2.执行属性转换
        Class<?> toType = writeMethod.getParameterTypes()[0];
        Object attValueObject = ConverterUtils.convert(toType, value);
        //3.执行属性注入
        try {
            writeMethod.invoke(object, attValueObject);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**执行字段注入，除了注入int,short,long,等基本类型之外该方法还支持注入枚举类型。注意：该方法会根据属性类型进行尝试类型转换 */
    public static boolean writeField(final Object object, final String property, final Object value) {
        if (object == null || property == null) {
            return false;
        }
        //1.查找方法
        Class<?> defineType = object.getClass();
        Field writeField = getField(defineType, property);
        if (writeField == null) {
            return false;
        }
        //2.执行属性转换
        Class<?> toType = writeField.getType();
        Object attValueObject = ConverterUtils.convert(toType, value);
        //3.执行属性注入
        try {
            writeField.setAccessible(true);
            writeField.set(object, attValueObject);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** 执行属性读取 */
    public static Object readProperty(final Object object, final String property) {
        if (object == null || property == null) {
            return false;
        }
        //1.查找方法
        Class<?> defineType = object.getClass();
        Method readMethod = getReadMethod(defineType, property);
        if (readMethod == null) {
            return null;
        }
        //2.执行属性读取
        try {
            return readMethod.invoke(object);
        } catch (Exception e) {
            return null;
        }
    }

    /** 执行字段读取 */
    public static Object readField(final Object object, final String property) {
        if (object == null || property == null) {
            return null;
        }
        //1.查找方法
        Class<?> defineType = object.getClass();
        Field readField = getField(defineType, property);
        if (readField == null) {
            return null;
        }
        //2.执行字段读取
        try {
            readField.setAccessible(true);
            return readField.get(object);
        } catch (Exception e) {
            return null;
        }
    }

    /** */
    public static Class<?> getPropertyType(final Class<?> target, final String property) {
        Property propertyFunc = getPropertyFunc(target, property);
        if (propertyFunc == null) {
            return null;
        }

        return getPropertyType(propertyFunc);
    }

    public static Map<String, Class<?>> getPropertyType(final Class<?> target) {
        Map<String, Class<?>> result = new LinkedHashMap<>();

        Map<String, Property> propertyMap = getPropertyFunc(target, test -> true);
        propertyMap.forEach((name, property) -> {
            result.put(name, getPropertyType(property));
        });

        return result;
    }

    public static Class<?> getPropertyType(Property property) {
        if (property == null) {
            return null;
        }

        AccessibleObject readerHandler = ((SimpleProperty) property).getReaderHandler();
        AccessibleObject writerHandler = ((SimpleProperty) property).getWriterHandler();
        if (readerHandler instanceof Method) {
            return ((Method) readerHandler).getReturnType();
        } else if (readerHandler instanceof Field) {
            return ((Field) readerHandler).getType();
        } else if (writerHandler instanceof Method) {
            return ((Method) writerHandler).getParameterTypes()[0];
        } else if (writerHandler instanceof Field) {
            return ((Field) writerHandler).getType();
        } else {
            return null;
        }
    }

    /** */
    public static Class<?> getFieldType(final Class<?> target, final String property) {
        Field readField = getField(target, property);
        if (readField != null) {
            return readField.getType();
        }
        return null;
    }

    /*----------------------------------------------------------------------------------------*/

    public static String toProperty(SFunction<?> property) {
        if (property == null) {
            return null;
        }

        Method targetMethod = MethodUtils.lambdaMethodName(property);
        if (targetMethod == null) {
            return null;
        }

        String methodName = targetMethod.getName();
        String attr;
        if (methodName.startsWith("get") || methodName.startsWith("set")) {
            attr = methodName.substring(3);
        } else if (methodName.startsWith("is")) {
            attr = methodName.substring(2);
        } else {
            return null;
        }

        return StringUtils.firstCharToLowerCase(attr);
    }

    /*----------------------------------------------------------------------------------------*/

    /** */
    public static void copyProperties(final Object dest, final Object orig) {
        if (dest == null) {
            throw new IllegalArgumentException("dest is null");
        }
        if (orig == null) {
            throw new IllegalArgumentException("orig is null");
        }

        List<String> propNames = new ArrayList<>();
        if (orig instanceof Map) {
            for (Object key : ((Map) orig).keySet()) {
                propNames.add(key.toString());
            }
        } else {
            propNames = getProperties(orig.getClass());
        }
        for (String prop : propNames) {
            copyProperty(dest, orig, prop);
        }
    }

    /** */
    public static void copyProperty(final Object dest, final Object orig, final String property) {
        if (dest == null) {
            throw new IllegalArgumentException("dest is null");
        }
        if (orig == null) {
            throw new IllegalArgumentException("orig is null");
        }
        if (StringUtils.isBlank(property)) {
            throw new IllegalArgumentException("property is null");
        }

        if (!(orig instanceof Map)) {
            if (!canReadProperty(orig.getClass(), property)) {
                return;
            }
        }
        if (!(dest instanceof Map)) {
            if (!canWriteProperty(dest.getClass(), property)) {
                return;
            }
        }

        Object val = null;
        if (!(orig instanceof Map)) {
            val = readProperty(orig, property);
        } else {
            val = ((Map) orig).get(property);
        }

        if (!(dest instanceof Map)) {
            writeProperty(dest, property, val);
        } else {
            ((Map) dest).put(property, val);
        }
    }
}

class SimpleProperty implements Property {
    private Field  field;
    private Method readerMethod;
    private Method writerMethod;

    public void setReader(Method readerMethod) {
        this.readerMethod = readerMethod;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public void setWriter(Method writerMethod) {
        this.writerMethod = writerMethod;
    }

    public Field getField() {
        return this.field;
    }

    public AccessibleObject getReaderHandler() {
        if (this.readerMethod != null) {
            return this.readerMethod;
        } else if (this.field != null) {
            return this.field;
        } else {
            return null;
        }
    }

    public AccessibleObject getWriterHandler() {
        if (this.writerMethod != null) {
            return this.writerMethod;
        } else if (this.field != null) {
            return this.field;
        } else {
            return null;
        }
    }

    @Override
    public boolean isReadOnly() {
        return this.writerMethod == null && this.field == null;
    }

    @Override
    public Object get(Object instance) {
        try {
            if (this.readerMethod != null) {
                return this.readerMethod.invoke(instance);
            } else if (this.field != null) {
                return this.field.get(instance);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void set(Object instance, Object value) {
        try {
            if (this.writerMethod != null) {
                this.writerMethod.invoke(instance, value);
            } else if (this.field != null) {
                this.field.set(instance, value);
            }
        } catch (Exception e) {
        }
    }
}

class MapProperty implements Property {
    private final String property;

    public MapProperty(String property) {
        this.property = property;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public Object get(Object instance) {
        return ((Map) instance).get(this.property);
    }

    @Override
    public void set(Object instance, Object value) {
        ((Map) instance).put(this.property, value);
    }
}