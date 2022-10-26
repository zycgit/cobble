package net.hasor.cobble.ref;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class OpsRatioTest {
    @Test
    public void iteratorlocks() {
        RandomRatio<String> rr = new RandomRatio<>();
        rr.addRatio(10, "I");
        rr.addRatio(20, "U");
        rr.addRatio(30, "D");

        Map<String, AtomicInteger> totalRun = new HashMap<>();
        for (int i = 0; i < 1000000; i++) {
            totalRun.computeIfAbsent(rr.getByRandom(), s -> new AtomicInteger()).incrementAndGet();
        }

        Map<String, AtomicInteger> methodRun = new HashMap<>();
        totalRun.forEach((key, cnt) -> {
            methodRun.computeIfAbsent(key, k -> new AtomicInteger(0)).getAndAdd(cnt.get());
        });

        System.out.println(totalRun);
    }
}