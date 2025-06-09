/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.cobble.io;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

/**
 * Tests for {@link IOUtils}.
 *
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
