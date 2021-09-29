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
import net.hasor.cobble.function.ESupplier;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 扫描的资源
 * @version : 2021-09-12
 * @author 赵永春 (zyc@hasor.net)
 */
public class ScanEvent {
    private final String                              name;
    private final long                                size;
    private final URL                                 resource;
    private final ESupplier<InputStream, IOException> stream;

    public ScanEvent(String name, long size, URL resource, ESupplier<InputStream, IOException> stream) {
        this.name = name;
        this.size = size;
        this.resource = resource;
        this.stream = stream;
    }

    public String getName() {
        return this.name;
    }

    public long getSize() {
        return this.size;
    }

    public URL getResource() {
        return this.resource;
    }

    public InputStream getStream() throws IOException {
        return this.stream.eGet();
    }
}
