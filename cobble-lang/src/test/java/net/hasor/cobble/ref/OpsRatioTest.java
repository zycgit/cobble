/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.ref;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

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
