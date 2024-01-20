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
import net.hasor.cobble.ExceptionUtils;
import net.hasor.cobble.StringUtils;
import net.hasor.cobble.function.Callable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
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
            if (loader != null) {
                t.setContextClassLoader(loader);
            }
            t.setName(String.format(template, cnt.incrementAndGet()));
            t.setDaemon(isDaemon);
            return t;
        };
    }

    private static Thread runnableRun(boolean run, Thread thread) {
        if (run) {
            thread.start();
        }
        return thread;
    }

    private static Thread runnableThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setName(String.format("Thread-%s", globalCnt.incrementAndGet()));
        return thread;
    }

    private static Thread callableThread(Callable runnable) {
        Thread thread = new Thread(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw ExceptionUtils.toRuntime(e);
            }
        });
        thread.setName(String.format("Thread-%s", globalCnt.incrementAndGet()));
        return thread;
    }

    /** create front Thread, but not start */
    public static Thread frontThread(boolean run, Runnable runnable) {
        Thread t = runnableThread(runnable);
        t.setDaemon(false);
        return runnableRun(run, t);
    }

    /** create front Thread, but not start */
    public static Thread frontThread(boolean run, Callable runnable) {
        Thread t = callableThread(runnable);
        t.setDaemon(false);
        return runnableRun(run, t);
    }

    /** create front Thread, but not start */
    public static Thread frontThread(boolean run, ClassLoader loader, Runnable runnable) {
        Thread t = runnableThread(runnable);
        t.setDaemon(false);
        t.setContextClassLoader(loader);
        return runnableRun(run, t);
    }

    /** create front Thread, but not start */
    public static Thread frontThread(boolean run, ClassLoader loader, Callable runnable) {
        Thread t = callableThread(runnable);
        t.setDaemon(false);
        t.setContextClassLoader(loader);
        return runnableRun(run, t);
    }

    /** create daemon Thread, but not start */
    public static Thread daemonThread(boolean run, Runnable runnable) {
        Thread t = runnableThread(runnable);
        t.setDaemon(true);
        return runnableRun(run, t);
    }

    /** create daemon Thread, but not start */
    public static Thread daemonThread(boolean run, Callable runnable) {
        Thread t = callableThread(runnable);
        t.setDaemon(true);
        return runnableRun(run, t);
    }

    /** create daemon Thread, but not start */
    public static Thread daemonThread(boolean run, ClassLoader loader, Runnable runnable) {
        Thread t = runnableThread(runnable);
        t.setDaemon(true);
        t.setContextClassLoader(loader);
        return runnableRun(run, t);
    }

    /** create daemon Thread, but not start */
    public static Thread daemonThread(boolean run, ClassLoader loader, Callable runnable) {
        Thread t = callableThread(runnable);
        t.setDaemon(true);
        t.setContextClassLoader(loader);
        return runnableRun(run, t);
    }

    /** create ExecutorService use nThreads daemon Thread*/
    public static ExecutorService daemonFixedThreadPool(final ClassLoader loader, int nThreads) {
        ThreadFactory threadFactory = threadFactory(loader, "Thread-%s", true);
        return Executors.newFixedThreadPool(nThreads, threadFactory);
    }

    /** create ExecutorService use nThreads front Thread*/
    public static ExecutorService frontFixedThreadPool(final ClassLoader loader, int nThreads) {
        ThreadFactory threadFactory = threadFactory(loader, "Thread-%s", false);
        return Executors.newFixedThreadPool(nThreads, threadFactory);
    }

    public static void sleep(long interval, TimeUnit timeUnit) {
        if (interval <= 0) {
            return;
        }
        try {
            Thread.sleep(timeUnit.toMillis(interval));
        } catch (Exception ignored) {
        }
    }

    public static void sleep(long intervalMillis) {
        if (intervalMillis <= 0) {
            return;
        }
        try {
            Thread.sleep(intervalMillis);
        } catch (Exception ignored) {
        }
    }
}