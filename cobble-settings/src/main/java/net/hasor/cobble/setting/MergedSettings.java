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
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import net.hasor.cobble.ResourcesUtils;
import net.hasor.cobble.io.input.AutoCloseInputStream;
import net.hasor.cobble.io.input.AutoCloseReader;
import net.hasor.cobble.setting.provider.SettingsReader;
import net.hasor.cobble.setting.provider.StreamType;
import net.hasor.cobble.setting.provider.properties.PropertiesSettingsReader;
import net.hasor.cobble.setting.provider.xml.XmlSettingsReader;
import net.hasor.cobble.setting.provider.yaml.YamlSettingsReader;

/***
 * 传入{@link InputStream}的方式获取{@link Settings}接口的支持。
 * @version : 2013-9-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class MergedSettings extends BasicSettings implements IOSettings {

    public MergedSettings() {
    }

    public void loadResource(String mainSettings, StreamType type) throws IOException {
        try (InputStream inStream = ResourcesUtils.getResourceAsStream(mainSettings)) {
            if (inStream != null) {
                this.loadConfigSource(new InputStreamReader(inStream, StandardCharsets.UTF_8), type);
            }
        }
    }

    public void loadStream(InputStream mainSettings, StreamType type) throws IOException {
        if (mainSettings != null) {
            this.loadConfigSource(new InputStreamReader(new AutoCloseInputStream(mainSettings), StandardCharsets.UTF_8), type);
        }
    }

    public void loadStream(InputStream mainSettings, Charset charset, StreamType type) throws IOException {
        if (mainSettings != null) {
            this.loadConfigSource(new InputStreamReader(new AutoCloseInputStream(mainSettings), charset), type);
        }
    }

    public void loadStringBody(String mainSettings, StreamType type) throws IOException {
        if (mainSettings != null) {
            this.loadConfigSource(new StringReader(mainSettings), type);
        }
    }

    public void loadReader(Reader mainSettings, StreamType type) throws IOException {
        if (mainSettings != null) {
            this.loadConfigSource(new AutoCloseReader(mainSettings), type);
        }
    }

    private void loadConfigSource(Reader dataReader, StreamType type) throws IOException {
        SettingsReader reader = switch (type) {
            case Xml -> new XmlSettingsReader();
            case Yaml -> new YamlSettingsReader();
            case Properties -> new PropertiesSettingsReader();
            default -> throw new UnsupportedOperationException(type + " Unsupported.");
        };
        reader.readSetting(dataReader, this);
        this.updateSettings();
    }
}
