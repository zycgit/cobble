/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.loader;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Loader 基类
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-09-29
 */
public abstract class AbstractResourceLoader implements ResourceLoader {
    public <T> List<T> scanResources(MatchType matchType, Scanner<T> scanner, String[] scanPaths) throws IOException {
        return this.scanResources(matchType, t -> true, scanner, scanPaths);
    }

    public <T> T scanOneResource(MatchType matchType, Scanner<T> scanner, String[] scanPaths) throws IOException {
        return this.scanOneResource(matchType, t -> true, scanner, scanPaths);
    }

    protected <T> Predicate<T>[] buildPredicate(MatchType matchType, String[] scanPaths, Function<T, String> nameFunc) {
        if (matchType == null) {
            throw new IllegalStateException("missing matchType.");
        }
        Predicate<T>[] tests = new Predicate[scanPaths.length];
        for (int i = 0; i < scanPaths.length; i++) {
            final String matchExpr = scanPaths[i];
            tests[i] = switch (matchType) {
                case None -> e -> true;
                case Match -> e -> nameFunc.apply(e).equals(matchExpr);
                case Regex -> e -> nameFunc.apply(e).matches(matchExpr);
                case ContainsAny -> e -> nameFunc.apply(e).contains(matchExpr);
                case Prefix -> e -> nameFunc.apply(e).startsWith(matchExpr);
                case Suffix -> e -> nameFunc.apply(e).endsWith(matchExpr);
                default -> e -> false;
            };
        }
        return tests;
    }

    @Override
    public CobbleClassLoader toClassLoader(ClassLoader parent) {
        return new CobbleClassLoader(parent, this);
    }

    @Override
    public void close() throws IOException {
        // no-op
    }
}
