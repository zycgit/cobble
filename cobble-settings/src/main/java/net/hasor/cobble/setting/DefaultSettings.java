/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import net.hasor.cobble.io.input.AutoCloseInputStream;
import net.hasor.cobble.setting.provider.StreamType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 继承自{@link MergedSettings}父类。
 * @version : 2013-9-9
 * @author 赵永春 (zyc@hasor.net)
 */
public class DefaultSettings extends MergedSettings {
    private URI settingURI;

    /**创建{@link DefaultSettings}类型对象。*/
    public DefaultSettings() throws IOException, URISyntaxException {
        this("");
    }

    /**创建{@link DefaultSettings}类型对象。*/
    public DefaultSettings(Reader mainSettings, StreamType type) throws IOException {
        this.addReader(mainSettings, type);
        refresh();
    }

    /**创建{@link DefaultSettings}类型对象。*/
    public DefaultSettings(String mainSettings) throws IOException, URISyntaxException {
        if (StringUtils.isNotBlank(mainSettings)) {
            URL url = ResourcesUtils.getResource(mainSettings);
            if (url != null) {
                this.settingURI = url.toURI();
            }
        }
        refresh();
    }

    /**创建{@link DefaultSettings}类型对象。*/
    public DefaultSettings(final File mainSettings) throws IOException {
        if (mainSettings != null) {
            this.settingURI = mainSettings.toURI();
        }
        refresh();
    }

    /**创建{@link DefaultSettings}类型对象。*/
    public DefaultSettings(final URI mainSettings) throws IOException {
        if (mainSettings != null) {
            this.settingURI = mainSettings;
        }
        refresh();
    }

    /**获取配置文件{@link URI}。*/
    public URI getSettingURI() {
        return this.settingURI;
    }

    @Override
    protected void readyLoad() throws IOException {
        super.readyLoad();
        URI settingConfig = getSettingURI();
        if (settingConfig != null) {
            try (InputStream stream = ResourcesUtils.getResourceAsStream(settingConfig)) {
                if (stream != null) {
                    logger.info("addConfig '" + settingConfig + "'");
                    _addStream(settingConfig.toURL());
                } else {
                    logger.error("not found " + settingConfig);
                }
            }
        }
    }

    private void _addStream(URL resourceUrl) throws IOException {
        AutoCloseInputStream inputStream = new AutoCloseInputStream(resourceUrl.openStream());
        String lowerCase = resourceUrl.toString().toLowerCase();

        if (lowerCase.endsWith(".xml")) {
            this.addStream(inputStream, StreamType.Xml);
        } else if (lowerCase.endsWith(".yaml") || lowerCase.endsWith(".yml")) {
            this.addStream(inputStream, StreamType.Yaml);
        } else {
            this.addStream(inputStream, StreamType.Properties);
        }
    }

    @Override
    public void refresh() throws IOException {
        logger.info("refresh -> cleanData and loadSettings...");
        this.cleanData();
        this.loadSettings();
    }
}
