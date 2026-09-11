/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.loader.jar;
import java.net.URLStreamHandler;
import java.net.spi.URLStreamHandlerProvider;
import net.hasor.cobble.StringUtils;

/**
 * {@link URLStreamHandler} for loader {@link JarFile}s.
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @see JarFile#registerUrlProtocolHandler()
 * @since 1.0.0
 */
public class HandlerProvider extends URLStreamHandlerProvider {
    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (StringUtils.equalsIgnoreCase(protocol, "jar")) {
            return new Handler();
        } else {
            return null;
        }
    }
}
