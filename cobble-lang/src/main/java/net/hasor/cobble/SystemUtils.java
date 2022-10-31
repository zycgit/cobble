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
import java.util.Locale;

/**
 * @since 1.0
 */
public final class SystemUtils {
    private static final boolean IS_WINDOWS       = isWindows0();
    private static final boolean IS_ANDROID       = isAndroid0();
    private static final boolean IS_OSX           = isOsx0();
    private static final boolean IS_J9_JVM        = isJ9Jvm0();
    private static final boolean IS_IVKVM_DOT_NET = isIkvmDotNet0();
    private static final boolean IS_SUPER_USER    = maybeSuperUser0();

    //--------------------------------------------------------------------

    /**
     * <p><code>NumberUtils</code> instances should NOT be constructed in standard programming.
     * Instead, the class should be used as <code>NumberUtils.stringToInt("6");</code>.</p>
     *
     * <p>This constructor is public to permit tools that require a JavaBean instance
     * to operate.</p>
     */
    public SystemUtils() {
        super();
    }

    //--------------------------------------------------------------------

    /**
     * Returns the value of the Java system property with the specified
     * {@code key}, while falling back to {@code null} if the property access fails.
     *
     * @return the property value or {@code null}
     */
    public static String getSystemProperty(String key) {
        return getSystemProperty(key, null);
    }

    /**
     * Returns the value of the Java system property with the specified
     * {@code key}, while falling back to the specified default value if
     * the property access fails.
     *
     * @return the property value.
     *         {@code def} if there's no such property or if an access to the
     *         specified property is not allowed.
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
     * Returns {@code true} if the running JVM is either <a href="https://developer.ibm.com/javasdk/">IBM J9</a> or
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
}
