/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.io;

import java.io.IOException;
import java.io.StringReader;
import org.junit.Test;

/**
 * Tests for {@link IOUtils}.
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class IOUtilsTest {
    @Test
    public void readToString() throws IOException {
        String str1 = "01234567890abcdefghijklmnopqrstuvwxyz";
        String str2 = IOUtils.readToString(new StringReader(str1), 10);

        assert !str1.equals(str2);
        assert str2.equals("0123456789");
    }
}
