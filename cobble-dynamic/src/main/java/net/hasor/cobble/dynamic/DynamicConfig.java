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
package net.hasor.cobble.dynamic;
import net.hasor.cobble.ArrayUtils;
import net.hasor.cobble.BeanUtils;
import net.hasor.cobble.ExceptionUtils;
import net.hasor.cobble.StringUtils;
import net.hasor.cobble.supplier.Provider;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 *
 * @version : 2014年9月7日
 * @author 赵永春 (zyc@hasor.net)
 */
public class DynamicConfig {
    private final Class<?>                         superClass;
    private final Map<String, DynamicPropertyInfo> dynamicPropertyMap = new HashMap<>();
    private final Map<String, MethodInterceptor[]> interceptorMap     = new HashMap<>();
    private final Map<String, Method>              interceptorMethods = new HashMap<>();
    private final Map<Class<?>, InvocationHandler> implementMap       = new HashMap<>();
    private       File                             debugOutputDir;

    /** 创建 {@link DynamicConfig} 类型对象 */
    public DynamicConfig() {
        this(BasicObject.class);
    }

    /** 创建 {@link DynamicConfig} 类型对象 */
    public DynamicConfig(Class<?> superClass) {
        this.superClass = superClass == null ? BasicObject.class : superClass;
        try {
            this.tryInitAop();
        } catch (Exception e) {
            throw ExceptionUtils.toRuntime(e);
        }
    }

    /** 动态添加一个属性，并且生成可以属性的get/set方法 */
    public void addProperty(String propertyName, Class<?> propertyType) {
        this.addProperty(propertyName, propertyType, ReadWriteType.ReadWrite);
    }

    /**
     * 动态添加一个属性，并且生成可以属性的get/set方法
     * @param propertyName 属性名
     * @param propertyType 属性类型
     * @param rwType 读写权限
     */
    public void addProperty(String propertyName, Class<?> propertyType, ReadWriteType rwType) {
        Object defaultValue = BeanUtils.getDefaultValue(propertyType);
        SimpleDynamicProperty dynamic = new SimpleDynamicProperty(defaultValue);
        this.addProperty(propertyName, propertyType, Provider.of(dynamic), rwType);
    }

    /**
     * 动态添加一个属性，并且生成可以属性的get/set方法
     * @param propertyName 属性名
     * @param propertyType 属性类型
     * @param delegate 属性默认值
     */
    public void addProperty(String propertyName, Class<?> propertyType, Supplier<? extends DynamicProperty> delegate) {
        this.addProperty(propertyName, propertyType, delegate, ReadWriteType.ReadWrite);
    }

    /**
     * 动态添加一个属性，并且生成可以属性的get/set方法
     * @param propertyName 属性名
     * @param propertyType 属性类型
     * @param delegate 属性默认值
     * @param rwType 读写权限
     */
    public void addProperty(String propertyName, Class<?> propertyType, Supplier<? extends DynamicProperty> delegate, ReadWriteType rwType) {
        Objects.requireNonNull(propertyType, "args propertyType is null.");
        Objects.requireNonNull(delegate, "args delegate is null.");
        Objects.requireNonNull(rwType, "args rwType is null.");
        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("args propertyName is null.");
        }

        // 如果存在这个属性，则抛出异常
        if (BeanUtils.hasProperty(this.getSuperClass(), propertyName)) {
            throw new IllegalStateException(propertyName + " get/set already exists");
        }
        this.dynamicPropertyMap.put(propertyName, new DynamicPropertyInfo(propertyType, delegate, rwType));
    }

    Supplier<? extends DynamicProperty> findDynamicProperty(String name) {
        return this.dynamicPropertyMap.get(name).delegateSupplier;
    }

    Map<String, DynamicPropertyInfo> getDynamicPropertyMap() {
        return Collections.unmodifiableMap(this.dynamicPropertyMap);
    }

    /** 添加Aop拦截器 */
    public void addAopInterceptor(Predicate<Method> matcher, MethodInterceptor... interceptors) {
        Objects.requireNonNull(matcher, "args matcher is null.");
        Objects.requireNonNull(interceptors, "args interceptors is null.");

        Method[] targetMethodArrays = this.getSuperClass().getMethods();
        for (Method targetMethod : targetMethodArrays) {
            int dataModifiers = targetMethod.getModifiers();
            if (Modifier.isPrivate(dataModifiers) || Modifier.isFinal(dataModifiers) || Modifier.isStatic(dataModifiers)) {
                continue;
            }

            if (!matcher.test(targetMethod)) {
                continue;
            }

            String interceptorMethodDesc = AsmTools.toAsmFullDesc(targetMethod);
            MethodInterceptor[] interceptorArray;
            if (this.interceptorMap.containsKey(interceptorMethodDesc)) {
                interceptorArray = this.interceptorMap.get(interceptorMethodDesc);
                interceptorArray = (MethodInterceptor[]) ArrayUtils.addAll(interceptorArray, interceptors);
            } else {
                interceptorArray = interceptors;
            }
            this.interceptorMap.put(interceptorMethodDesc, interceptorArray);
            this.interceptorMethods.put(interceptorMethodDesc, targetMethod);
        }
    }

    /** 根据方法查找这个方法的所有拦截器 */
    MethodInterceptor[] findInterceptor(String tmDesc) {
        return this.interceptorMap.get(tmDesc);
    }

    Map<String, MethodInterceptor[]> getInterceptorMap() {
        return Collections.unmodifiableMap(this.interceptorMap);
    }

    Map<String, Method> getInterceptorMethods() {
        return Collections.unmodifiableMap(this.interceptorMethods);
    }

    private void tryInitAop() throws ReflectiveOperationException {
        // class
        Set<Class<? extends MethodInterceptor>> classLevels = extractAopType(this.superClass);
        for (Class<? extends MethodInterceptor> interceptorType : classLevels) {
            this.addAopInterceptor(t -> true, interceptorType.newInstance());
        }

        // method
        Method[] targetMethodArrays = this.getSuperClass().getMethods();
        for (Method method : targetMethodArrays) {
            Set<Class<? extends MethodInterceptor>> methodLevels = extractAopType(method);
            for (Class<? extends MethodInterceptor> interceptorType : methodLevels) {
                if (classLevels.contains(interceptorType)) {
                    continue;
                }

                this.addAopInterceptor(t -> t.equals(method), interceptorType.newInstance());
            }
        }
    }

    private Set<Class<? extends MethodInterceptor>> extractAopType(AnnotatedElement element) {
        Annotation[] annotations = element.getDeclaredAnnotations();
        List<Annotation> annotationList = new ArrayList<>();
        for (Annotation annotation : annotations) {
            extractAopAnnotation(annotation, annotationList, new ArrayList<>());
        }

        Set<Class<? extends MethodInterceptor>> config = new HashSet<>();
        for (Annotation annotation : annotationList) {
            config.addAll(Arrays.asList(((Aop) annotation).value()));
        }
        return config;
    }

    private static void extractAopAnnotation(Annotation data, List<Annotation> annotationList, List<Annotation> visitList) {
        if (visitList.contains(data)) {
            return;
        } else {
            visitList.add(data);
        }

        if (data instanceof Aop) {
            annotationList.add(data);
        } else if (data instanceof AopSet) {
            annotationList.addAll(Arrays.asList(((AopSet) data).value()));
        } else {
            Annotation[] annotations = data.annotationType().getAnnotations();
            for (Annotation annotation : annotations) {
                extractAopAnnotation(annotation, annotationList, visitList);
            }
        }
    }

    /** 添加动态 接口实现 */
    public void addImplements(Class<?> interfaceType, InvocationHandler handler) {
        Objects.requireNonNull(interfaceType, "args interfaceType is null.");
        Objects.requireNonNull(handler, "args handler is null.");

        this.implementMap.put(interfaceType, handler);
    }

    Map<Class<?>, InvocationHandler> getImplementMap() {
        return Collections.unmodifiableMap(this.implementMap);
    }

    // --------------------------------------------------------------------------------------------

    /** 是否包含改变 */
    public boolean hasChange() {
        return !this.interceptorMap.isEmpty() || !this.dynamicPropertyMap.isEmpty() || !this.implementMap.isEmpty();
    }

    /** 是否支持Aop */
    public boolean isSupport() {
        String resName = this.superClass.getName().replace(".", "/") + ".class";
        if (resName.startsWith("java/") || resName.startsWith("javax/")) {
            return false;
        } else {
            return !Modifier.isPrivate(this.superClass.getModifiers());
        }
    }

    /** 父类类型 */
    public Class<?> getSuperClass() {
        return superClass;
    }

    File getDebugOutputDir() {
        return debugOutputDir;
    }

    public void setDebugOutputDir(File debugOutputDir) {
        this.debugOutputDir = debugOutputDir;
    }

    static class DynamicPropertyInfo {
        Supplier<? extends DynamicProperty> delegateSupplier;
        Class<?>                            propertyType;
        ReadWriteType                       rwType;

        public DynamicPropertyInfo(Class<?> propertyType, Supplier<? extends DynamicProperty> delegateSupplier, ReadWriteType rwType) {
            this.propertyType = propertyType;
            this.delegateSupplier = delegateSupplier;
            this.rwType = rwType;
        }
    }
}