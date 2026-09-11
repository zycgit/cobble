/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.loader;
import java.util.HashSet;
import java.util.Set;
import net.hasor.cobble.loader.providers.ClassPathResourceLoader;

/**
 * ResourceLoader 的 ClassFinder 接口实现
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-09-29
 */
public class CobbleClassScanner extends CobbleResourceScanner {

    private final CobbleClassLoader classLoader;

    /** Scanner using ClassPathResourceLoader scan,load class using new ClassLoader */
    public CobbleClassScanner(ClassLoader parent) {
        super(new ClassPathResourceLoader());
        this.classLoader = this.loader.toClassLoader(parent);
    }

    /** Scanner using ClassPathResourceLoader scan,load class using new ClassLoader */
    public CobbleClassScanner(ClassPathResourceLoader loader) {
        super(loader);
        this.classLoader = this.loader.toClassLoader(loader.getClassLoader());
    }

    /** Scanner using ResourceLoader scan,load class using new ClassLoader */
    public CobbleClassScanner(ClassLoader parent, ResourceLoader loader) {
        super(loader);
        this.classLoader = this.loader.toClassLoader(parent);
    }

    /** Scanner using loader scan,load class using loader */
    public CobbleClassScanner(CobbleClassLoader loader) {
        super(loader.getResourceLoader());
        this.classLoader = loader;
    }

    /**
     * 扫描jar包中凡是匹配compareType参数的类均被返回。（对执行结果不缓存）
     * @param matcherType 匹配的类型，可以是类标注的注解、实现的接口、继承的父类。
     * @return 返回扫描结果。
     */
    public Set<Class<?>> getClassSet(String[] loadPackages, Class<?> matcherType) {
        return this.getClassSet(loadPackages, context -> {
            ClassMatcher.ClassInfo classInfo = context.getClassInfo();
            String matcherTypeName = matcherType.getName();

            if (classInfo != null) {
                if (matcherType.isAnnotation()) {
                    for (String anno : classInfo.annos) {
                        if (anno.equals(matcherTypeName)) {
                            return true;
                        }
                    }
                } else if (matcherType.isInterface()) {
                    for (String anno : classInfo.interFaces) {
                        if (anno.equals(matcherTypeName)) {
                            return true;
                        }
                    }
                } else {
                    for (String castType : classInfo.castType) {
                        if (castType.equals(matcherTypeName)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        });
    }

    /**
     * 扫描jar包中凡是匹配compareType参数的类均被返回。（对执行结果不缓存）
     * @param matcher 匹配规则。
     * @return 返回扫描结果。
     */
    @Deprecated
    public Set<Class<?>> getClassSet(ClassMatcher matcher) {
        throw new IllegalArgumentException("Class scan requires package ranges, use getClassSet(String[], ClassMatcher).");
    }

    /**
     * 扫描jar包中凡是匹配compareType参数的类均被返回。（对执行结果不缓存）
     * @param matcher 匹配规则。
     * @return 返回扫描结果。
     */
    public Set<Class<?>> getClassSet(String[] loadPackages, ClassMatcher matcher) {
        Set<String> classNamesSet = this.getClassNamesSet(loadPackages, matcher);
        Set<Class<?>> classes = new HashSet<>();
        try {
            for (String className : classNamesSet) {
                classes.add(this.classLoader.loadClass(className));
            }
            return classes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
