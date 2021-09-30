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
package net.hasor.cobble.loader;
import java.io.IOException;
import java.util.Set;

/**
 *
 * @version : 2021-09-12
 * @author 赵永春 (zyc@hasor.net)
 */
public interface ClassFinder {
    /**
     * 扫描jar包中凡是匹配compareType参数的类均被返回。（对执行结果不缓存）
     * @param matcher 匹配规则。
     * @return 返回扫描结果。
     */
    public default Set<Class<?>> getClassSet(ClassMatcher matcher) {
        return this.getClassSet(new String[0], matcher);
    }

    /**
     * 扫描jar包中凡是匹配compareType参数的类均被返回。（对执行结果不缓存）
     * @param matcher 匹配规则。
     * @return 返回扫描结果。
     */
    public Set<Class<?>> getClassSet(String[] loadPackages, ClassMatcher matcher);

    public ClassInfo loadClassInfo(String className) throws IOException;

    /** 类信息结构 */
    public static class ClassInfo {
        public String   className  = null;
        public String   superName  = null;
        public String[] interFaces = new String[0];
        public String[] castType   = new String[0];
        public String[] annos      = new String[0];
    }

    public static abstract class ClassMatcherContext {
        private final ClassInfo classInfo;

        public ClassMatcherContext(ClassInfo classInfo) {
            this.classInfo = classInfo;
        }

        public ClassInfo getClassInfo() {
            return this.classInfo;
        }

        public abstract ClassInfo loadClass(String className) throws IOException;
    }

    @FunctionalInterface
    public static interface ClassMatcher {
        public boolean matcher(ClassMatcherContext context) throws IOException;
    }
}
