package net.hasor.cobble.concurrent.timer;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

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
