/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
import java.util.function.Function;
import export.FooForApp2;
import lib.Foo;

public class ApplicationForApp2 implements Function<String, String> {
    @Override
    public String apply(String msg) {
        Foo foo = new FooForApp2();
        return foo.run(msg);
    }
}
