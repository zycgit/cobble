/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble;
import net.hasor.cobble.concurrent.QoSBucket;
import org.junit.Test;

/**
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2015年4月5日
 */
public class QosTest {
    private void runSpeedTest(QoSBucket qoSBucket) throws Throwable {
        //QoS：稳定每秒50，峰值1000，时间更新窗口10毫秒。
        //
        int i = 0;
        int realLoops = 0;
        long startTime = System.currentTimeMillis() / 1000;
        Thread.sleep(1000);
        do {
            realLoops++;
            if (qoSBucket.check()) {
                i++;
                long checkTime = System.currentTimeMillis() / 1000;
                System.out.println("Count:" + i + "\tSpeed(s):" + (i / (checkTime - startTime)) + "\trealLoops: " + realLoops);
            }
        } while (i != 300);
    }

    @Test
    public void speedTest_1() throws Throwable {
        //QoS：稳定每秒50，峰值1000，时间更新窗口10毫秒。
        runSpeedTest(new QoSBucket(50, 1000, 10));
    }

    @Test
    public void speedTest_2() throws Throwable {
        //QoS：稳定每秒50，峰值1000，时间更新窗口10毫秒。
        runSpeedTest(new QoSBucket(50));
    }
}
