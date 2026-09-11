/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package export;
import lib.Foo;

public class FooForApp2 implements Foo {
    @Override
    public String run(String... args) {
        return "FooForApp2 running...";
    }
}
