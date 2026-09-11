/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble;
import java.io.File;
import java.util.List;
import net.hasor.cobble.io.FileUtils;
import org.junit.Test;

/***
 * 创建 JDBC
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class FileUtilsTest {
    @Test
    public void walk_01() {
        List<File> walk = FileUtils.walk(new File(""), false, t -> true);
        System.out.println(walk);
    }

}
