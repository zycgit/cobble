/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.loader.providers;
import net.hasor.cobble.loader.ResourceLoader;

/**
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2024-11-26
 */
public interface MergedLoader {
    ResourceLoader findLoader(String resource);
}
