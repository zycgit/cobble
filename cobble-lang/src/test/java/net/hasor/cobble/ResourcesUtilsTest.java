/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;

/**
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-04-19
 */
public class ResourcesUtilsTest {
    @Test
    public void getResourceTest1() throws IOException {
        InputStream resource = ResourcesUtils.getResourceAsStream("file:src/test/resources/i18n/authlabel_en_US.properties");
        assert resource != null;
    }

    @Test
    public void getResourceTest2() throws IOException {
        InputStream resource = ResourcesUtils.getResourceAsStream("classpath:/META-INF/License.ASF20.txt");
        assert resource != null;
    }
}
