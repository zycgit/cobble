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
package net.hasor.cobble.loader.providers;
import net.hasor.cobble.StringUtils;
import net.hasor.cobble.io.FilenameUtils;
import net.hasor.cobble.loader.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 *
 * @version : 2021-09-12
 * @author 赵永春 (zyc@hasor.net)
 */
public class ExportResourceLoader extends AbstractResourceLoader {
    private       boolean        exportAll     = false;
    private final Set<String>    exportPathSet = new HashSet<>();
    private final ResourceLoader resourceLoader;

    public ExportResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public boolean isExportAll() {
        return this.exportAll;
    }

    public void setExportAll(boolean exportAll) {
        this.exportAll = exportAll;
    }

    public void addExport(String exportPath) {
        if (StringUtils.isNotEmpty(exportPath)) {
            if (exportPath.charAt(exportPath.length() - 1) != '/') {
                exportPath = exportPath + '/';
            }
            this.exportPathSet.add(fmtDirPath(exportPath));
        }
    }

    public Set<String> getExportSet() {
        return this.exportPathSet;
    }

    private static String fmtDirPath(String dirPath) {
        dirPath = FilenameUtils.getPath(dirPath).replace("\\", "/");
        if (dirPath.length() > 0 && dirPath.charAt(0) == '/') {
            dirPath = dirPath.substring(1);
        }
        return dirPath;
    }

    protected boolean testPath(String dirPath) {
        if (this.exportAll) {
            return true;
        } else if (this.exportPathSet.isEmpty()) {
            return false;
        }

        dirPath = fmtDirPath(dirPath);
        if (this.exportPathSet.contains(dirPath)) {
            return true;
        }
        int index = dirPath.length();
        while (true) {
            index = dirPath.lastIndexOf("/", index);
            if (index == -1) {
                break;
            }
            dirPath = dirPath.substring(0, index);
            if (this.exportPathSet.contains(dirPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public <T> List<T> scanResources(MatchType matchType, Scanner<T> scanner, String[] scanPaths) throws IOException {
        if (scanPaths.length == 0) {
            scanPaths = exportPathSet.toArray(new String[0]);
        } else {
            scanPaths = Arrays.stream(scanPaths).filter(this::testPath).toArray(String[]::new);
        }
        if (scanPaths.length == 0) {
            return Collections.emptyList();
        }
        return this.resourceLoader.scanResources(matchType, scanner, scanPaths);
    }

    @Override
    public <T> T scanOneResource(MatchType matchType, Scanner<T> scanner, String[] scanPaths) throws IOException {
        if (scanPaths.length == 0) {
            scanPaths = exportPathSet.toArray(new String[0]);
        } else {
            scanPaths = Arrays.stream(scanPaths).filter(this::testPath).toArray(String[]::new);
        }
        if (scanPaths.length == 0) {
            return null;
        }
        return this.resourceLoader.scanOneResource(matchType, scanner, scanPaths);
    }

    @Override
    public URL getResource(String resource) throws IOException {
        if (!testPath(resource)) {
            return null;
        }
        return this.resourceLoader.getResource(resource);
    }

    @Override
    public InputStream getResourceAsStream(String resource) throws IOException {
        if (!testPath(resource)) {
            return null;
        }
        return this.resourceLoader.getResourceAsStream(resource);
    }

    @Override
    public List<URL> getResources(String resource) throws IOException {
        if (!testPath(resource)) {
            return Collections.emptyList();
        }
        return this.resourceLoader.getResources(resource);
    }

    @Override
    public List<InputStream> getResourcesAsStream(String resource) throws IOException {
        if (!testPath(resource)) {
            return Collections.emptyList();
        }
        return this.resourceLoader.getResourcesAsStream(resource);
    }

    @Override
    public boolean exist(String resource) {
        if (!testPath(resource)) {
            return false;
        }
        return this.resourceLoader.exist(resource);
    }
}
