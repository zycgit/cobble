/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.loader;
import java.util.function.Predicate;
import net.hasor.cobble.loader.ClassMatcher.ClassInfo;

/**
 * 扫描过程中的匹配器
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-10-01
 */
public class ClassMatchers {
    private ClassMatchers() {
    }

    /** 任意类型 */
    public static Predicate<?> any() {
        return t -> true;
    }

    /** 类型 或 类上有 xx 注解 */
    public static ClassMatcher featureWith(final Class<?> annotationType) {
        String featureType = annotationType.getName();
        return context -> {
            ClassInfo classInfo = context.getClassInfo();
            for (String castType : classInfo.castType) {
                if (castType.equals(featureType)) {
                    return true;
                }
            }
            for (String face : classInfo.annos) {
                if (face.equals(featureType)) {
                    return true;
                }
            }
            return false;
        };
    }

    /** 类上需要有 xx 注解 */
    public static ClassMatcher annotatedWith(final Class<?> annotationType) {
        String featureType = annotationType.getName();
        return context -> {
            ClassInfo classInfo = context.getClassInfo();
            for (String face : classInfo.annos) {
                if (face.equals(featureType)) {
                    return true;
                }
            }
            return false;
        };
    }

    /** 子类 */
    public static ClassMatcher subClassesOf(final Class<?> superclass) {
        String testType = superclass.getName();
        return context -> {
            ClassInfo classInfo = context.getClassInfo();
            if (classInfo.className.equals(testType)) {
                return false;
            }
            for (String castType : classInfo.castType) {
                if (castType.equals(testType)) {
                    return true;
                }
            }
            return false;
        };
    }
}
