/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble;
import java.math.BigInteger;
import org.junit.Test;

/***
 * 创建 JDBC
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class RandomUtilsTest {
    @Test
    public void nextLongTest_01() {
        for (int i = 0; i < 1000000; i++) {
            long ranLong = RandomUtils.nextBigInteger(BigInteger.valueOf(-100), BigInteger.valueOf(100)).longValue();
            if (-100 <= ranLong && ranLong <= 100) {
                assert true;
            } else {
                System.out.println(ranLong);
                assert false;
            }
        }
    }

    @Test
    public void nextLongTest_02() {
        for (int i = 0; i < 1000000; i++) {
            long ranLong = RandomUtils.nextBigInteger(BigInteger.valueOf(100), BigInteger.valueOf(200)).longValue();
            if (100 <= ranLong && ranLong <= 200) {
                assert true;
            } else {
                System.out.println(ranLong);
                assert false;
            }
        }
    }
}
