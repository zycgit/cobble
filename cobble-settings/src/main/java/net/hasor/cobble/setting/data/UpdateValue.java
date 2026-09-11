/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.setting.data;
import net.hasor.cobble.setting.Settings;

/**
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2014年10月11日
 */
public interface UpdateValue {
    void update(DataNode dataNode, Settings context);
}
