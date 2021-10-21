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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 *
 * @version : 2011-6-3
 * @author 赵永春 (zyc@hasor.net)
 */
public class BeanUtils {
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

    /** 查找一个可操作的方法列表 */
    public static List<Method> getMethods(final Class<?> type) {
        return Arrays.asList(type.getMethods());
    }

    /** 查找一个可操作的字段 */
    public static Field getField(final String fieldName, final Class<?> type) {
        if (fieldName == null || type == null) {
            return null;
        }
        for (Field f : type.getFields()) {
            if (f.getName().equals(fieldName)) {
                return f;
            }
        }
        return null;
    }

    /** 查找一个可操作的方法 */
    public static Method getMethod(final Class<?> atClass, final String name, final Class<?>[] paramType) {
        try {
            return atClass.getMethod(name, paramType);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 该方法的作用是反射的形式调用目标的方法。
     * @param target 被调用的对象
     * @param methodName 要调用的反射方法名。
     * @param objects 参数列表
     */
    public static Object invokeMethod(final Object target, final String methodName, final Object... objects) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (target == null) {
            return null;
        }
        Class<?> targetType = target.getClass();
        Method invokeMethod = null;
        //反射调用方法
        Method[] ms = targetType.getMethods();
        for (Method m : ms) {
            //1.名字不相等的忽略
            if (!m.getName().equals(methodName)) {
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
            throw new NullPointerException(methodName + " invokeMethod is null.");
        } else {
            return invokeMethod.invoke(target, objects);
        }
    }
    /*----------------------------------------------------------------------------------------*/

    /** 获取类定义的字段和继承父类中定义的字段以及父类的父类（子类重新定义同名字段也会被列入集合） */
    public static List<Field> findALLFields(final Class<?> target) {
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

    /** 获取类定义的方法和继承父类中定义的方法以及父类的父类（子类的重写方法也会被返回） */
    public static List<Method> findALLMethods(final Class<?> target) {
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
    public static Method getReadMethod(final String property, final Class<?> target) {
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
    public static Method getWriteMethod(final String property, final Class<?> target) {
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
    public static Map<String, Property> getPropertyFunc(final Class<?> target) {
        Map<String, Property> propertyMap = new LinkedHashMap<>();
        List<Method> ms = getMethods(target);
        for (Method m : ms) {
            if (m.getDeclaringClass() == Object.class) {
                continue;
            }

            String name = m.getName();
            if (name.startsWith("get")) {
                name = name.substring(3);
                ((SimpleProperty) propertyMap.computeIfAbsent(name, s -> new SimpleProperty())).setReader(m);
            } else if (name.startsWith("is")) {
                name = name.substring(2);
                ((SimpleProperty) propertyMap.computeIfAbsent(name, s -> new SimpleProperty())).setReader(m);
            } else if (name.startsWith("set")) {
                name = name.substring(3);
                ((SimpleProperty) propertyMap.computeIfAbsent(name, s -> new SimpleProperty())).setWriter(m);
            }
        }

        return propertyMap;
    }

    /** 获取属性名集合，被包含的属性可能有些只是只读属性，有些是只写属性。也有读写属性 */
    public static Map<String, Property> getFieldFunc(final Class<?> target) {
        Map<String, Property> propertyMap = new LinkedHashMap<>();
        for (Field f : target.getFields()) {
            ((SimpleProperty) propertyMap.computeIfAbsent(f.getName(), s -> new SimpleProperty())).setReader(f);
            if (!Modifier.isFinal(f.getModifiers())) {
                ((SimpleProperty) propertyMap.computeIfAbsent(f.getName(), s -> new SimpleProperty())).setWriter(f);
            }
        }

        return propertyMap;
    }

    /** 测试是否支持 readProperty 方法, 返回 true 表示可以进行读取操作 */
    public static boolean canReadProperty(final String property, final Class<?> target) {
        Method readMethod = getReadMethod(property, target);
        return readMethod != null;
    }

    /** 测试是否支持 writeProperty 方法, 返回 true 表示可以进行写入操作 */
    public static boolean canWriteProperty(final String property, final Class<?> target) {
        Method writeMethod = getWriteMethod(property, target);
        return writeMethod != null;
    }

    /** 测试是否支持 Field 方法写 */
    public static boolean canWriteField(final String property, final Class<?> target) {
        Field field = getField(property, target);
        return field != null && !Modifier.isFinal(field.getModifiers());
    }

    /** 测试是否支持 Field 方法读 */
    public static boolean canReadField(final String property, final Class<?> target) {
        return true;
    }

    /** 测试是否具有 propertyName 所表示的属性, 无论是读或写方法只要存在一个就表示存在该属性 */
    public static boolean hasProperty(final String propertyName, final Class<?> target) {
        //get、set方法
        if (getReadMethod(propertyName, target) == null) {
            return getWriteMethod(propertyName, target) != null;
        }
        return true;
    }

    /** 测试是否具有 fieldName 所表示的字段, 无论是读或写方法只要存在一个就表示存在该属性 */
    public static boolean hasField(final String propertyName, final Class<?> target) {
        return getField(propertyName, target) != null;
    }

    /**执行属性注入，除了注入int,short,long,等基本类型之外该方法还支持注入枚举类型。返回值表示执行是否成功。注意：该方法会根据属性类型进行尝试类型转换。*/
    public static boolean writeProperty(final Object object, final String attName, final Object value) {
        if (object == null || attName == null) {
            return false;
        }
        //1.查找方法
        Class<?> defineType = object.getClass();
        Method writeMethod = getWriteMethod(attName, defineType);
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

    /**执行字段注入，除了注入int,short,long,等基本类型之外该方法还支持注入枚举类型。注意：该方法会根据属性类型进行尝试类型转换。*/
    public static boolean writeField(final Object object, final String fieldName, final Object value) {
        if (object == null || fieldName == null) {
            return false;
        }
        //1.查找方法
        Class<?> defineType = object.getClass();
        Field writeField = getField(fieldName, defineType);
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
    public static Object readProperty(final Object object, final String attName) {
        if (object == null || attName == null) {
            return false;
        }
        //1.查找方法
        Class<?> defineType = object.getClass();
        Method readMethod = getReadMethod(attName, defineType);
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
    public static Object readField(final Object object, final String fieldName) {
        if (object == null || fieldName == null) {
            return null;
        }
        //1.查找方法
        Class<?> defineType = object.getClass();
        Field readField = getField(fieldName, defineType);
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

    /***/
    public static Class<?> getPropertyType(final Class<?> defineType, final String attName) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(attName, defineType);
            return pd.getPropertyType();
        } catch (Exception e) {
            Method readMethod = getReadMethod(attName, defineType);
            if (readMethod != null) {
                return readMethod.getReturnType();
            } else {
                Method writeMethod = getWriteMethod(attName, defineType);
                if (writeMethod != null) {
                    return writeMethod.getParameterTypes()[0];
                }
            }
            return null;
        }
    }

    /***/
    public static Class<?> getFieldType(final Class<?> defineType, final String attName) {
        Field readField = getField(attName, defineType);
        if (readField != null) {
            return readField.getType();
        }
        return null;
    }
 
    /***/
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

    /***/
    public static void copyProperty(final Object dest, final Object orig, final String propertyName) {
        if (dest == null) {
            throw new IllegalArgumentException("dest is null");
        }
        if (orig == null) {
            throw new IllegalArgumentException("orig is null");
        }
        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("propertyName is null");
        }

        if (!(orig instanceof Map)) {
            if (!canReadProperty(propertyName, orig.getClass())) {
                return;
            }
        }
        if (!(dest instanceof Map)) {
            if (!canWriteProperty(propertyName, dest.getClass())) {
                return;
            }
        }

        Object val = null;
        if (!(orig instanceof Map)) {
            val = readProperty(orig, propertyName);
        } else {
            val = ((Map) orig).get(propertyName);
        }

        if (!(dest instanceof Map)) {
            writeProperty(dest, propertyName, val);
        } else {
            ((Map) dest).put(propertyName, val);
        }
    }

}

class SimpleProperty implements Property {
    private Field  readerField;
    private Field  writerField;
    private Method readerMethod;
    private Method writerMethod;

    public void setReader(Method readerMethod) {
        this.readerMethod = readerMethod;
    }

    public void setReader(Field readerField) {
        this.readerField = readerField;
    }

    public void setWriter(Method writerMethod) {
        this.writerMethod = writerMethod;
    }

    public void setWriter(Field writerField) {
        this.writerField = writerField;
    }

    @Override
    public Object get(Object instance) {
        try {
            if (this.readerMethod != null) {
                return this.readerMethod.invoke(instance);
            } else if (this.readerField != null) {
                return this.readerField.get(instance);
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
            } else if (this.writerField != null) {
                this.writerField.set(instance, value);
            }
        } catch (Exception e) {
        }
    }
}