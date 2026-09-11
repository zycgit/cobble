/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.loader;
import java.io.IOException;

/**
 * 资源加载器、资源扫描器
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-09-29
 */
@FunctionalInterface
public interface ClassMatcher {
    boolean matcher(ClassMatcherContext context) throws IOException;

    /** 类信息结构 */
    class ClassInfo {
        public String   className  = null;
        public String   superName  = null;
        public String[] interFaces = new String[0];
        public String[] castType   = new String[0];
        public String[] annos      = new String[0];
    }

    abstract class ClassMatcherContext {
        private final ClassInfo classInfo;

        public ClassMatcherContext(ClassInfo classInfo) {
            this.classInfo = classInfo;
        }

        public ClassInfo getClassInfo() {
            return this.classInfo;
        }

        public abstract ClassInfo loadClass(String className) throws IOException;
    }
}
