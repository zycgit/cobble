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
import net.hasor.cobble.loader.MatchType;
import net.hasor.cobble.loader.ResourceLoader;
import net.hasor.cobble.loader.Scanner;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Loader 基类
 * @version : 2021-09-29
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractResourceLoader implements ResourceLoader {
    public ClassLoader toClassLoader() {
        return new LoaderClassLoader(this);
    }

    public ClassLoader toClassLoader(ClassLoader parent) {
        return new LoaderClassLoader(this, parent);
    }

    public ClassFinder toClassFinder() {
        return new LoaderClassScanner(this, this.toClassLoader());
    }

    public ClassFinder toClassFinder(ClassLoader parent) {
        return new LoaderClassScanner(this, this.toClassLoader(parent));
    }

    public <T> List<T> scanResources(Scanner<T> scanner) throws IOException {
        return scanResources(MatchType.None, scanner, new String[0]);
    }

    public <T> List<T> scanResources(MatchType matchType, Scanner<T> scanner) throws IOException {
        return scanResources(matchType, scanner, new String[0]);
    }

    public <T> T scanOneResource(Scanner<T> scanner) throws IOException {
        return scanOneResource(MatchType.None, scanner, new String[0]);
    }

    public <T> T scanOneResource(MatchType matchType, Scanner<T> scanner) throws IOException {
        return scanOneResource(matchType, scanner, new String[0]);
    }

    protected <T> Predicate<T>[] buildPredicate(MatchType matchType, String[] scanPackages, Function<T, String> nameFunc) {
        if (matchType == null) {
            throw new IllegalStateException("missing matchType.");
        }
        Predicate<T>[] tests = new Predicate[scanPackages.length];
        for (int i = 0; i < scanPackages.length; i++) {
            final String matchExpr = scanPackages[i];
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
