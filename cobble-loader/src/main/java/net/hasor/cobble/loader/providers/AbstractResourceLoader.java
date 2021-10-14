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
package net.hasor.cobble.loader.providers;
import net.hasor.cobble.loader.ClassFinder;
import net.hasor.cobble.loader.LoaderClassLoader;
import net.hasor.cobble.loader.LoaderClassScanner;
import net.hasor.cobble.loader.ResourceLoader;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Loader 基类
 * @version : 2021-09-29
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractResourceLoader implements ResourceLoader {
    private ClassLoader bindParentLoader;
    private ClassLoader bindClassLoader;
    private ClassFinder bindClassFinder;

    public void bindParentLoader(ClassLoader parent) {
        if (this.bindClassLoader != null && this.bindClassLoader != ClassLoader.getSystemClassLoader()) {
            throw new IllegalStateException("the bindClassLoader already exists.");
        } else {
            this.bindParentLoader = parent;
        }
    }

    protected ClassLoader createBindClassLoader(ClassLoader bindParentLoader) {
        return new LoaderClassLoader(this, bindParentLoader);
    }

    public ClassLoader toClassLoader() {
        if (this.bindClassLoader == null) {
            synchronized (this) {
                if (this.bindClassLoader != null) {
                    return this.bindClassLoader;
                } else {
                    ClassLoader useBindLoader = this.bindParentLoader;
                    if (useBindLoader == null) {
                        useBindLoader = ClassLoader.getSystemClassLoader();
                    }
                    this.bindClassLoader = Objects.requireNonNull(this.createBindClassLoader(useBindLoader));
                }
            }
        }
        return this.bindClassLoader;
    }

    protected ClassFinder createBindClassFinder() {
        return new LoaderClassScanner(this);
    }

    public ClassFinder toClassFinder() {
        if (this.bindClassFinder == null) {
            synchronized (this) {
                if (this.bindClassFinder != null) {
                    return this.bindClassFinder;
                } else {
                    this.bindClassFinder = this.createBindClassFinder();
                }
            }
        }
        return this.bindClassFinder;
    }

    protected <T> Predicate<T>[] buildPredicate(MatchType matchType, String[] scanPaths, Function<T, String> nameFunc) {
        if (matchType == null) {
            throw new IllegalStateException("missing matchType.");
        }
        Predicate<T>[] tests = new Predicate[scanPaths.length];
        for (int i = 0; i < scanPaths.length; i++) {
            final String matchExpr = scanPaths[i];
            switch (matchType) {
                case None: {
                    tests[i] = e -> true;
                    break;
                }
                case Match: {
                    tests[i] = e -> nameFunc.apply(e).equals(matchExpr);
                    break;
                }
                case Regex: {
                    tests[i] = e -> nameFunc.apply(e).matches(matchExpr);
                    break;
                }
                case ContainsAny: {
                    tests[i] = e -> nameFunc.apply(e).contains(matchExpr);
                    break;
                }
                case Prefix: {
                    tests[i] = e -> nameFunc.apply(e).startsWith(matchExpr);
                    break;
                }
                case Suffix: {
                    tests[i] = e -> nameFunc.apply(e).endsWith(matchExpr);
                    break;
                }
                default: {
                    tests[i] = e -> false;
                    break;
                }
            }
        }
        return tests;
    }
}
