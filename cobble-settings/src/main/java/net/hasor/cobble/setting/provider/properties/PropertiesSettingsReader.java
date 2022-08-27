/*
 *
 * * Copyright 2008-2016 the original author or authors.
 * *
 * * Licensed under the Apache License, Version 2.0 (the "License");
 * * you may not use this file except in compliance with the License.
 * * You may obtain a copy of the License at
 * *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * *
 * * Unless required by applicable law or agreed to in writing, software
 * * distributed under the License is distributed on an "AS IS" BASIS,
 * * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * * See the License for the specific language governing permissions and
 * * limitations under the License.
 *
 */
package net.hasor.cobble.setting.provider.properties;
import net.hasor.cobble.setting.Settings;
import net.hasor.cobble.setting.provider.ResourceReader;
import net.hasor.cobble.setting.provider.SettingsReader;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

/**
 *
 * @version : 2021-02-01
 * @author 赵永春 (zyc@byshell.org)
 */
public class PropertiesSettingsReader implements SettingsReader {
    @Override
    public void readSetting(ResourceReader resourceReader, Settings readTo) throws IOException {
        Reader reader = resourceReader.getReader();
        Properties properties = new Properties();
        properties.load(reader);
        loadProperties(readTo, properties);
    }

    protected void loadProperties(Settings readTo, Properties properties) {
        properties.forEach((k, v) -> {
            readTo.addSetting(k.toString(), v, Settings.DefaultNameSpace);
        });
    }
}
