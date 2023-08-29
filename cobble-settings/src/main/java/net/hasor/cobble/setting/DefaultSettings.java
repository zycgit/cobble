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
import net.hasor.cobble.StringUtils;
import net.hasor.cobble.setting.provider.StreamType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 继承自{@link MergedSettings}父类。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2013-9-9
 */
public class DefaultSettings extends MergedSettings {
    private URI settingURI;

    /** 创建{@link DefaultSettings}类型对象。 */
    public DefaultSettings() throws IOException {
        refresh();
    }

    /** 创建{@link DefaultSettings}类型对象。 */
    public DefaultSettings(String mainSettings) throws IOException, URISyntaxException {
        if (StringUtils.isNotBlank(mainSettings)) {
            this.settingURI = new URI("classpath:" + mainSettings);
        }
        refresh();
    }

    /** 创建{@link DefaultSettings}类型对象。 */
    public DefaultSettings(File mainSettings) throws IOException {
        if (mainSettings != null) {
            this.settingURI = mainSettings.toURI();
        }
        refresh();
    }

    /** 创建{@link DefaultSettings}类型对象。 */
    public DefaultSettings(URI mainSettings) throws IOException {
        if (mainSettings != null) {
            this.settingURI = mainSettings;
        }
        refresh();
    }

    /** 获取配置文件{@link URI}。 */
    public URI getSettingURI() {
        return this.settingURI;
    }

    @Override
    protected void loadSettings() throws IOException {
        super.loadSettings();

        URI configURI = getSettingURI();
        if (configURI != null) {
            try (InputStream stream = ResourcesUtils.getResourceAsStream(configURI)) {
                if (stream != null) {
                    String lowerCase = configURI.toString().toLowerCase();
                    if (lowerCase.endsWith(".xml")) {
                        this.loadStream(stream, StreamType.Xml);
                    } else if (lowerCase.endsWith(".yaml") || lowerCase.endsWith(".yml")) {
                        this.loadStream(stream, StreamType.Yaml);
                    } else {
                        this.loadStream(stream, StreamType.Properties);
                    }
                }
            }
        }

        super.loadSettings();
    }
}
