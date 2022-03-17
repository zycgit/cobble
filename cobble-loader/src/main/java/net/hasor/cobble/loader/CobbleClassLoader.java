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
import net.hasor.cobble.loader.ResourceLoader.MatchType;
import net.hasor.cobble.loader.ResourceLoader.Scanner;
import net.hasor.cobble.logging.Logger;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * ResourceLoader 转 ClassLoader
 * @version : 2021-09-29
 * @author 赵永春 (zyc@hasor.net)
 */
public class CobbleClassLoader extends ClassLoader {
    private static final Logger               logger        = Logger.getLogger(CobbleClassLoader.class);
    private final        String               tempDirectory = "cobbleLoader_" + System.currentTimeMillis();
    private              File                 tempDir;
    private final        List<ResourceLoader> loaders       = new ArrayList<>();

    public CobbleClassLoader(ClassLoader parent) {
        super(parent);
    }

    public CobbleClassLoader(ClassLoader parent, ResourceLoader resourceLoader) {
        super(parent);
        this.addResourceLoader(resourceLoader);//new String[0],
    }

    public void addResourceLoader(ResourceLoader loader) {
        //isol? String[] packages,
        if (loader != null && !this.loaders.contains(loader)) {
            this.loaders.add(loader);
        }
    }

    protected <T> T scanOneResources(MatchType matchType, Scanner<T> scanner, String[] scanPaths) {
        for (ResourceLoader loader : this.loaders) {
            try {
                T result = loader.scanOneResource(matchType, scanner, scanPaths);
                if (result != null) {
                    return result;
                }
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
        return null;
    }

    protected <T> List<T> scanResources(MatchType matchType, Scanner<T> scanner, String[] scanPaths) {
        List<T> result = new ArrayList<>();
        for (ResourceLoader loader : this.loaders) {
            try {
                List<T> itemList = loader.scanResources(matchType, scanner, scanPaths);
                if (itemList != null) {
                    result.addAll(itemList);
                }
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
        return result;
    }

    protected ResourceLoader findResourceLoader(String resource) {
        for (ResourceLoader loader : this.loaders) {
            if (loader.exist(resource)) {
                return loader;
            }
        }
        return null;
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        String resource = className.replace(".", "/") + ".class";
        ResourceLoader loader = findResourceLoader(resource);
        if (loader != null) {
            try {
                InputStream inStream = loader.getResourceAsStream(resource);
                if (inStream != null) {
                    ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                    IOUtils.copy(inStream, byteOutput);
                    byte[] bs = byteOutput.toByteArray();
                    return this.defineClass(className, bs, 0, bs.length);
                }
            } catch (IOException e2) {
                throw new ClassNotFoundException(className, e2);
            }
        }
        return super.findClass(className);
    }

    @Override
    protected URL findResource(String resource) {
        ResourceLoader loader = findResourceLoader(resource);
        if (loader != null) {
            try {
                return loader.getResource(resource);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                return null;
            }
        }
        return null;
    }

    @Override
    public Enumeration<URL> findResources(String resource) throws IOException {
        List<URL> resultList = new ArrayList<>();

        for (ResourceLoader loader : this.loaders) {
            List<URL> resources = loader.getResources(resource);
            if (resources != null) {
                resultList.addAll(resources);
            }
        }

        return CollectionUtils.asEnumeration(resultList);
    }

    public InputStream getResourceAsStream(String resource) {
        ResourceLoader loader = findResourceLoader(resource);
        if (loader != null) {
            try {
                return loader.getResourceAsStream(resource);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                return null;
            }
        }
        return null;
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
            } else {
                return super.findLibrary(sLib);
            }
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

        for (ResourceLoader loader : this.loaders) {
            File foundFile = loader.scanOneResource(MatchType.Suffix, event -> {
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

            if (foundFile != null && foundFile.exists()) {
                return foundFile;
            }
        }
        return null;
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