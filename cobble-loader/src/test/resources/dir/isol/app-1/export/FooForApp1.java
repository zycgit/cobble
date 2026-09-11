/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package export;
import lib.Foo;

public class FooForApp1 implements Foo {
    @Override
    public String run(int mainArg, String... args) {
        return "FooForApp1 running...";
    }

    @Override
    public String version() {
        return "call version running...";
    }
}
