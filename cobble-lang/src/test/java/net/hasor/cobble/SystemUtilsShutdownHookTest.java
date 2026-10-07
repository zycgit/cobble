/*
 * Copyright 2008-2009 the original author or authors.
 * Licensed under the Apache License, Version 2.0.
 */
package net.hasor.cobble;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SystemUtilsShutdownHookTest {
    @Test(timeout = 20000)
    public void lastCancellationSerializesWithReplacementRegistration() throws Exception {
        assertEquals("replacement\n", runChild("race-state"));
    }

    @Test(timeout = 20000)
    public void concurrentReplacementActuallyRunsAtJvmExit() throws Exception {
        assertEquals("replacement\n", runChild("race-exit"));
    }

    @Test(timeout = 20000)
    public void cancellationAndReregistrationPreserveOnlyLiveCallbacks() throws Exception {
        assertEquals("first\nsecond\n", runChild("lifecycle"));
    }

    @Test(timeout = 20000)
    public void throwingCallbackDoesNotPreventFollowingCallbacks() throws Exception {
        assertEquals("rejected\nfollowing\n", runChild("shutdown"));
    }

    @Test(timeout = 20000)
    public void failedNativeRemovalKeepsTheAlreadyStartingHook() throws Exception {
        assertEquals("replacement\n", runChild("starting"));
    }

    private static String runChild(String mode) throws Exception {
        Path directory = Files.createTempDirectory("cobble-shutdown-");
        Path output = directory.resolve("callbacks.txt");
        Path log = directory.resolve("process.log");
        String classpath = new File(SystemUtilsShutdownHookTest.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath() + File.pathSeparator + new File(SystemUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        Process process = new ProcessBuilder(new File(System.getProperty("java.home"), "bin/java").getPath(), "-cp", classpath, ExitProcess.class.getName(), mode, output.toString()).redirectErrorStream(true).redirectOutput(log.toFile()).start();
        try {
            assertTrue("child did not exit", process.waitFor(12, TimeUnit.SECONDS));
            assertEquals(Files.readString(log), 0, process.exitValue());
            assertTrue("live callbacks did not run: " + Files.readString(log), Files.exists(output));
            return Files.readString(output);
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
                process.waitFor(3, TimeUnit.SECONDS);
            }
            Files.deleteIfExists(output);
            Files.deleteIfExists(log);
            Files.deleteIfExists(directory);
        }
    }

    public static class ExitProcess {
        public static void main(String[] args) throws Exception {
            Path output = Path.of(args[1]);
            switch (args[0]) {
                case "race-state":
                case "race-exit":
                    race(output, args[0].equals("race-state"));
                    break;
                case "lifecycle":
                    lifecycle(output);
                    break;
                case "starting":
                    AutoCloseable first = SystemUtils.registerShutdownHook(() -> append(output, "removed"));
                    Thread hook = (Thread) field("shutdownHookThread");
                    synchronized (field("SHUTDOWN_HOOK_LOCK")) {
                        daemon(() -> System.exit(0), "start-jvm-shutdown").start();
                        await(() -> blockedIn(hook, SystemUtils.class.getName(), "doShutdownHook"));
                        first.close();
                        // The JVM refuses removal after shutdown starts; its pending hook still owns callbacks.
                        SystemUtils.registerShutdownHook(() -> append(output, "replacement"));
                    }
                    break;
                case "shutdown":
                    SystemUtils.registerShutdownHook(() -> {
                        try {
                            SystemUtils.registerShutdownHook(() -> append(output, "late"));
                            throw new AssertionError("registration during shutdown succeeded");
                        } catch (IllegalStateException expected) {
                            append(output, "rejected");
                        }
                        throw new AssertionError("expected callback failure");
                    });
                    SystemUtils.registerShutdownHook(() -> append(output, "following"));
                    break;
                default:
                    throw new AssertionError(args[0]);
            }
        }

        private static void race(Path output, boolean verifyRegistry) throws Exception {
            AutoCloseable first = SystemUtils.registerShutdownHook(() -> append(output, "removed"));
            FutureTask<Void> removal = new FutureTask<>(() -> {
                first.close();
                return null;
            });
            FutureTask<AutoCloseable> registration = new FutureTask<>(() -> SystemUtils.registerShutdownHook(() -> append(output, "replacement")));
            Thread remover = daemon(removal, "remove-last-callback");
            Thread registrar = daemon(registration, "register-replacement-callback");
            // Pin the native-removal boundary without assuming that registration can bypass it.
            synchronized (Class.forName("java.lang.ApplicationShutdownHooks")) {
                remover.start();
                await(() -> blockedIn(remover, "java.lang.ApplicationShutdownHooks", "remove"));
                registrar.start();
                await(() -> !registrar.isAlive() || blockedIn(registrar, SystemUtils.class.getName(), "registerShutdownHook"));
            }
            removal.get(3, TimeUnit.SECONDS);
            registration.get(3, TimeUnit.SECONDS);
            if (verifyRegistry) {
                check(callbacks().size() == 1, "replacement callback is absent");
                check(field("shutdownHookThread") != null, "replacement has no native shutdown hook");
            }
        }

        private static void lifecycle(Path output) throws Exception {
            check(callbacks().isEmpty(), "unexpected initial callbacks");
            check(!SystemUtils.unregisterShutdownHook(null), "null removal succeeded");
            Callable<?> cancelled = () -> append(output, "cancelled");
            AutoCloseable first = SystemUtils.registerShutdownHook(cancelled);
            AutoCloseable duplicate = SystemUtils.registerShutdownHook(cancelled);
            check(callbacks().size() == 1, "duplicate registration changed set semantics");
            first.close();
            first.close();
            duplicate.close();
            check(callbacks().isEmpty(), "cancelled callback retained");
            check(field("shutdownHookThread") == null, "empty registry kept its native hook");
            SystemUtils.registerShutdownHook(() -> append(output, "first"));
            AutoCloseable middle = SystemUtils.registerShutdownHook(cancelled);
            SystemUtils.registerShutdownHook(() -> append(output, "second"));
            middle.close();
            check(callbacks().size() == 2, "unrelated registrations changed");
        }

        private static Thread daemon(Runnable task, String name) {
            Thread thread = new Thread(task, name);
            thread.setDaemon(true);
            return thread;
        }

        private static boolean blockedIn(Thread thread, String type, String method) {
            if (thread.getState() != Thread.State.BLOCKED) {
                return false;
            }
            for (StackTraceElement frame : thread.getStackTrace()) {
                if (frame.getClassName().equals(type) && frame.getMethodName().equals(method)) {
                    return true;
                }
            }
            return false;
        }

        private static void await(BooleanSupplier condition) throws InterruptedException {
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(3);
            while (!condition.getAsBoolean() && System.nanoTime() < deadline) {
                Thread.sleep(1);
            }
            check(condition.getAsBoolean(), "concurrent boundary was not reached");
        }

        private static Object field(String name) throws Exception {
            Field field = SystemUtils.class.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(null);
        }

        private static Collection<?> callbacks() throws Exception {
            return (Collection<?>) field("shutdownHooks");
        }

        private static Path append(Path output, String value) throws Exception {
            return Files.writeString(output, value + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }

        private static void check(boolean condition, String message) {
            if (!condition) {
                throw new AssertionError(message);
            }
        }
    }
}
