/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.cobble.concurrent;
import net.hasor.cobble.StringUtils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @version : 2015年3月28日
 * @author 赵永春 (zyc@hasor.net)
 */
public class ThreadUtils {
    private static final AtomicInteger globalCnt = new AtomicInteger(0);

    public static ThreadFactory daemonThreadFactory(final ClassLoader loader) {
        return threadFactory(loader, "Thread-%s", true);
    }

    public static ThreadFactory daemonThreadFactory(final ClassLoader loader, final String nameTemplate) {
        return threadFactory(loader, nameTemplate, true);
    }

    public static ThreadFactory threadFactory(final ClassLoader loader, final String nameTemplate, final boolean isDaemon) {
        final String template = StringUtils.isBlank(nameTemplate) ? "Thread-%s" : nameTemplate;
        final AtomicInteger cnt = new AtomicInteger(0);
        return run -> {
            Thread t = new Thread(run);
            t.setContextClassLoader(loader);
            t.setName(String.format(template, cnt.incrementAndGet()));
            t.setDaemon(isDaemon);
            return t;
        };
    }

    public static Thread frontThread(final ClassLoader loader, Runnable runnable) {
        Thread t = new Thread(runnable);
        t.setContextClassLoader(loader);
        t.setName(String.format("Thread-%s", globalCnt.incrementAndGet()));
        t.setDaemon(false);
        return t;
    }

    public static Thread daemonThread(final ClassLoader loader, Runnable runnable) {
        Thread t = new Thread(runnable);
        t.setContextClassLoader(loader);
        t.setName(String.format("Thread-%s", globalCnt.incrementAndGet()));
        t.setDaemon(true);
        return t;
    }
}