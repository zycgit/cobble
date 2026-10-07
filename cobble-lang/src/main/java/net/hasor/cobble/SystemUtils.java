/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.cobble;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import net.hasor.cobble.logging.Logger;
import net.hasor.cobble.logging.LoggerFactory;

/**
 * <p>
 * Helpers for {@code java.lang.System}.
 * </p>
 * <p>
 * If a system property cannot be read due to security restrictions, the corresponding field in this class will be set
 * to {@code null} and a message will be written to {@code System.err}.
 * </p>
 * <p>
 * #ThreadSafe#
 * </p>
 * 2021-11-01 reduction based on Apache Commons lang3, org.apache.commons.lang3.StringUtils
 * @author 赵永春 (zyc@hasor.net)
 * @version $Id: SystemUtils.java 1671043 2015-04-03 12:09:07Z britter $
 * @since 1.0
 */
public final class SystemUtils {
    private static final Logger           logger             = LoggerFactory.getLogger(SystemUtils.class);
    private static final Object           SHUTDOWN_HOOK_LOCK = new Object();
    private static final Set<Callable<?>> shutdownHooks      = new LinkedHashSet<>();
    private static       Thread           shutdownHookThread;
    private static       boolean          shutdownHookRunning;
    private static final boolean          IS_WINDOWS         = isWindows0();
    private static final boolean          IS_ANDROID         = isAndroid0();
    private static final boolean          IS_OSX             = isOsx0();
    private static final boolean          IS_J9_JVM          = isJ9Jvm0();
    private static final boolean          IS_IVKVM_DOT_NET   = isIkvmDotNet0();
    private static final boolean          IS_SUPER_USER      = maybeSuperUser0();

    //--------------------------------------------------------------------

    /**
     * <p><code>NumberUtils</code> instances should NOT be constructed in standard programming.
     * Instead, the class should be used as <code>NumberUtils.stringToInt("6");</code>.</p>
     * <p>This constructor is public to permit tools that require a JavaBean instance to operate.</p>
     */
    public SystemUtils() {
        super();
    }

    //--------------------------------------------------------------------

    /**
     * Returns the value of the Java system property with the specified
     * {@code key}, while falling back to {@code null} if the property access fails.
     * @return the property value or {@code null}
     */
    public static String getSystemProperty(String key) {
        return getSystemProperty(key, null);
    }

    /**
     * Returns the value of the Java system property with the specified {@code key},
     * while falling back to the specified default value if the property access fails.
     * @return the property value. {@code def} if there's no such property or if an access to the specified property is not allowed.
     */
    public static String getSystemProperty(final String key, String defaultValue) {
        ObjectUtils.checkNotNull(key, "key");
        if (key.isEmpty()) {
            throw new IllegalArgumentException("key must not be empty.");
        }

        String value = System.getProperty(key);
        if (value == null) {
            return defaultValue;
        }

        return value;
    }

    //--------------------------------------------------------------------

    /**
     * Register a callback that is invoked when the JVM is shutting down.
     * @param shutdownHook callback invoked during JVM shutdown.
     * @return handle used to unregister the callback.
     */
    public static AutoCloseable registerShutdownHook(Callable<?> shutdownHook) {
        Objects.requireNonNull(shutdownHook, "shutdownHook is null.");
        synchronized (SHUTDOWN_HOOK_LOCK) {
            if (shutdownHookRunning) {
                throw new IllegalStateException("Shutdown is already in progress.");
            }
            if (!shutdownHooks.add(shutdownHook)) {
                return new ShutdownHookRegistration(shutdownHook);
            }
            if (shutdownHookThread == null) {
                Thread hookThread = new Thread(SystemUtils::doShutdownHook, "Cobble-ShutdownHook");
                try {
                    Runtime.getRuntime().addShutdownHook(hookThread);
                    shutdownHookThread = hookThread;
                } catch (Throwable e) {
                    shutdownHooks.remove(shutdownHook);
                    throw e;
                }
            }
            return new ShutdownHookRegistration(shutdownHook);
        }
    }

    /**
     * Unregister a callback from JVM shutdown callbacks.
     * @param shutdownHook registered callback.
     * @return true if the callback was registered.
     */
    public static boolean unregisterShutdownHook(Callable<?> shutdownHook) {
        if (shutdownHook == null) {
            return false;
        }
        synchronized (SHUTDOWN_HOOK_LOCK) {
            boolean removed = shutdownHooks.remove(shutdownHook);
            if (!shutdownHookRunning && shutdownHooks.isEmpty() && shutdownHookThread != null) {
                // A replacement callback must not reuse a hook that is being removed.
                removeShutdownHook(shutdownHookThread);
            }
            return removed;
        }
    }

    private static void doShutdownHook() {
        Set<Callable<?>> hookSet = null;
        synchronized (SHUTDOWN_HOOK_LOCK) {
            shutdownHookRunning = true;
            hookSet = new LinkedHashSet<>(shutdownHooks);
            shutdownHooks.clear();
        }
        for (Callable<?> shutdownHook : hookSet) {
            try {
                shutdownHook.call();
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private static void removeShutdownHook(Thread hookThread) {
        try {
            Runtime.getRuntime().removeShutdownHook(hookThread);
            shutdownHookThread = null;
        } catch (IllegalStateException e) {
            if (!"Shutdown in progress".equals(e.getMessage())) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private static final class ShutdownHookRegistration implements AutoCloseable {
        private final Callable<?> shutdownHook;
        private       boolean     closed;

        private ShutdownHookRegistration(Callable<?> shutdownHook) {
            this.shutdownHook = shutdownHook;
        }

        @Override
        public void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            unregisterShutdownHook(this.shutdownHook);
        }
    }

    //--------------------------------------------------------------------

    /** Return {@code true} if the JVM is running on Windows */
    public static boolean isWindows() {
        return IS_WINDOWS;
    }

    public static boolean isAndroid() {
        return IS_ANDROID;
    }

    /** Return {@code true} if the JVM is running on OSX / MacOS */
    public static boolean isOsx() {
        return IS_OSX;
    }

    /**
     * Returns {@code true} if the running JVM is either
     * <a href="https://developer.ibm.com/javasdk/">IBM J9</a> or
     * <a href="https://www.eclipse.org/openj9/">Eclipse OpenJ9</a>, {@code false} otherwise.
     */
    public static boolean isJ9Jvm() {
        return IS_J9_JVM;
    }

    /** Returns {@code true} if the running JVM is <a href="https://www.ikvm.net">IKVM.NET</a>, {@code false} otherwise. */
    public static boolean isIkvmDotNet() {
        return IS_IVKVM_DOT_NET;
    }

    public static boolean isSupperUser() {
        return IS_SUPER_USER;
    }

    private static boolean isWindows0() {
        return getSystemProperty("os.name", "").toLowerCase(Locale.US).contains("win");
    }

    private static boolean isAndroid0() {
        // Idea: Sometimes java binaries include Android classes on the classpath, even if it isn't actually Android.
        // Rather than check if certain classes are present, just check the VM, which is tied to the JDK.

        // Optional improvement: check if `android.os.Build.VERSION` is >= 24. On later versions of Android, the
        // OpenJDK is used, which means `Unsafe` will actually work as expected.

        // Android sets this property to Dalvik, regardless of whether it actually is.
        String vmName = getSystemProperty("java.vm.name");
        return "Dalvik".equals(vmName);
    }

    private static boolean isOsx0() {
        String osname = getSystemProperty("os.name", "").toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", "");
        return osname.startsWith("macosx") || osname.startsWith("osx");
    }

    private static boolean isJ9Jvm0() {
        String vmName = getSystemProperty("java.vm.name", "").toLowerCase();
        return vmName.startsWith("ibm j9") || vmName.startsWith("eclipse openj9");
    }

    private static boolean isIkvmDotNet0() {
        String vmName = getSystemProperty("java.vm.name", "").toUpperCase(Locale.US);
        return vmName.equals("IKVM.NET");
    }

    private static boolean maybeSuperUser0() {
        String username = getSystemProperty("user.name");
        if (isWindows()) {
            return "Administrator".equals(username);
        }
        // Check for root and toor as some BSDs have a toor user that is basically the same as root.
        return "root".equals(username) || "toor".equals(username);
    }

    //--------------------------------------------------------------------
    private static final int JAVA_VERSION = javaVersion0();

    public static int getJavaVersion() {
        return JAVA_VERSION;
    }

    private static int javaVersion0() {
        if (isAndroid0()) {
            return 6;
        }

        String javaSpecVersion = getSystemProperty("java.specification.version", "1.6");
        final String[] components = javaSpecVersion.split("\\.");
        final int[] version = new int[components.length];
        for (int i = 0; i < components.length; i++) {
            version[i] = Integer.parseInt(components[i]);
        }

        if (version[0] == 1) {
            assert version[1] >= 6;
            return version[1];
        } else {
            return version[0];
        }
    }
}
