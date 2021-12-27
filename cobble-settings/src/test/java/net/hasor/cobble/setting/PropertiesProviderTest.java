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
package net.hasor.cobble.setting;
import net.hasor.cobble.ResourcesUtils;
import net.hasor.cobble.setting.provider.properties.PropertiesSettingsReader;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class PropertiesProviderTest {
    @Test
    public void propertiesTest() throws IOException {
        InputStream conf = ResourcesUtils.getResourceAsStream("/net_hasor_core_settings/simple-config.properties");
        InputStreamReader settingReader = new InputStreamReader(conf, StandardCharsets.UTF_8);

        PropertiesSettingsReader reader = new PropertiesSettingsReader();
        BasicSettings settings = new BasicSettings();
        reader.readSetting(() -> settingReader, settings);

        assert settings.getString("mySelf.myName").equals("赵永春");
        assert settings.getString("myself.myname") == null;
        assert settings.getInteger("mySelf.myAge") == 12;
        assert settings.getString("mySelf.myBirthday").equals("1986-01-01 00:00:00");
        assert settings.getString("mySelf.myWork").equals("Software Engineer");
        assert settings.getString("mySelf.myProjectURL").equals("http://www.hasor.net/");
        assert settings.getString("mySelf.source").equals("Prop");
    }
}
