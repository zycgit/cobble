/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package app.onlybasic;
import app.lib.Foo;

public class FooOnlyBasic implements Foo {
    @Override
    public String run(String... args) {
        return "FooOnlyBasic running...";
    }
}
