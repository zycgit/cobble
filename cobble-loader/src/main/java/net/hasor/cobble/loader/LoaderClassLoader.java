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
package net.hasor.cobble.loader;
import net.hasor.cobble.CollectionUtils;
import net.hasor.cobble.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @version : 2014年9月7日
 * @author 赵永春 (zyc@hasor.net)
 */
public class LoaderClassLoader extends ClassLoader {
    public static final String         tempDirectory = "LoaderClassLoader";
    private             File           tempDir;
    private final       ResourceLoader resourceLoader;

    public LoaderClassLoader(ResourceLoader resourceLoader, ClassLoader parent) {
        super(parent);
        this.resourceLoader = resourceLoader;
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        if (LoaderClassLoader.class.getName().equals(className)) {
            return LoaderClassLoader.class;
        }
        if (this.resourceLoader != null) {
            String resourceName = className.replace(".", "/") + ".class";
            InputStream inStream = this.getResourceAsStream(resourceName);
            if (inStream == null) {
                throw new ClassNotFoundException(className);
            } else {
                try {
                    ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                    IOUtils.copy(inStream, byteOutput);
                    byte[] bs = byteOutput.toByteArray();
                    return this.defineClass(className, bs, 0, bs.length);
                } catch (IOException e) {
                    throw new ClassNotFoundException(className, e);
                }
            }
        } else {
            return super.findClass(className);
        }
    }

    @Override
    protected URL findResource(String name) {
        try {
            if (this.resourceLoader != null) {
                URL resourceURL = this.resourceLoader.getResource(name);
                if (resourceURL != null) {
                    return resourceURL;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return super.findResource(name);
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        try {
            if (this.resourceLoader != null) {
                List<URL> resourceURLs = this.resourceLoader.getResources(name);
                if (resourceURLs != null && !resourceURLs.isEmpty()) {
                    return CollectionUtils.asEnumeration(resourceURLs);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return super.findResources(name);
    }

    public InputStream getResourceAsStream(String name) {
        InputStream inputStream = super.getResourceAsStream(name);
        if (inputStream != null) {
            return inputStream;
        } else {
            try {
                return this.resourceLoader != null ? this.resourceLoader.getResourceAsStream(name) : null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @see java.lang.ClassLoader#findLibrary(java.lang.String)
     * @return The absolute path of the native library.
     */
    @Override
    protected String findLibrary(String sLib) {
        try {
            File tempLib = findJarNativeEntry(sLib);
            if (tempLib != null) {
                return tempLib.getAbsolutePath();
            }
            return super.findLibrary(sLib);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Finds native library entry.
     *
     * @param sLib Library name. For example for the library name "Native"
     *  - Windows returns entry "Native.dll"
     *  - Linux returns entry "libNative.so"
     *  - Mac returns entry "libNative.jnilib" or "libNative.dylib"
     *    (depending on Apple or Oracle JDK and/or JDK version)
     * @return Native library entry.
     */
    private File findJarNativeEntry(String sLib) throws IOException {
        String sName = System.mapLibraryName(sLib);
        return this.resourceLoader.scanOneResource(ResourceLoader.MatchType.Suffix, event -> {
            // Example: sName is "Native.dll"
            String sEntry = event.getName(); // "Native.dll" or "abc/xyz/Native.dll"
            // sName "Native.dll" could be found, for example
            //   - in the path: abc/Native.dll/xyz/my.dll <-- do not load this one!
            //   - in the partial name: abc/aNative.dll   <-- do not load this one!
            String[] token = sEntry.split("/"); // the last token is library name
            if (token.length > 0 && token[token.length - 1].equals(sName)) {
                File fileTmp = createTempFile(event);
                fileTmp.deleteOnExit();
                return fileTmp;
            }
            return null;
        }, new String[] { sName });
    }

    /**
     * Using temp files (one per inner JAR/DLL) solves many issues:
     * 1. There are no ways to load JAR defined in a JarEntry directly
     *    into the JarFile object (see also #6 below).
     * 2. Cannot use memory-mapped files because they are using
     *    nio channels, which are not supported by JarFile ctor.
     * 3. JarFile object keeps opened JAR files handlers for fast access.
     * 4. Deep resource in a jar-in-jar does not have well defined URL.
     *    Making temp file with JAR solves this problem.
     * 5. Similar issues with native libraries:
     *    <code>ClassLoader.findLibrary()</code> accepts ONLY string with
     *    absolute path to the file with native library.
     * 6. Option "java.protocol.handler.pkgs" does not allow access to nested JARs(?).
     *
     * @param inf JAR entry information.
     * @return temporary file object presenting JAR entry.
     */
    private File createTempFile(ResourceLoader.ScanEvent inf) throws IOException {
        // Temp files directory:
        //   WinXP: C:/Documents and Settings/username/Local Settings/Temp/LoaderClassLoader
        //   Unix: /var/tmp/LoaderClassLoader
        if (tempDir == null) {
            File dir = new File(System.getProperty("java.io.tmpdir"), tempDirectory);
            if (!dir.exists()) {
                dir.mkdir();
            }
            chmod777(dir); // Unix - allow temp directory RW access to all users.
            if (!dir.exists() || !dir.isDirectory()) {
                throw new IOException("Cannot create temp directory " + dir.getAbsolutePath());
            }
            tempDir = dir;
        }
        File fileTmp = File.createTempFile(inf.getName() + ".", null, tempDir);
        fileTmp.deleteOnExit();
        chmod777(fileTmp); // Unix - allow temp file deletion by any user
        //
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileTmp)); InputStream in = inf.getStream()) {
            IOUtils.copy(in, out);
        }
        return fileTmp;
    }

    private void chmod777(File file) {
        file.setReadable(true, false);
        file.setWritable(true, false);
        file.setExecutable(true, false); // Unix: allow content for dir, redundant for file
    }
}