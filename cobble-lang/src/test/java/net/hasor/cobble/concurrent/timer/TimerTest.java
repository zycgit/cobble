/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.concurrent.timer;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class TimerTest {
    @Test
    public void timerTest() throws Throwable {
        Timer timer = new HashedWheelTimer();

        timer.newTimeout(timeout -> System.out.println("s1"), 1, TimeUnit.SECONDS);
        timer.newTimeout(timeout -> System.out.println("s2"), 2, TimeUnit.SECONDS);
        timer.newTimeout(timeout -> System.out.println("s3"), 3, TimeUnit.SECONDS);
        timer.newTimeout(timeout -> System.out.println("s4"), 4, TimeUnit.SECONDS);

        Thread.sleep(3500);
    }
}
