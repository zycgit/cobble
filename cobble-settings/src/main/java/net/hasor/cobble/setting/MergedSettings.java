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
import net.hasor.cobble.setting.provider.ConfigSource;
import net.hasor.cobble.setting.provider.ResourceReader;
import net.hasor.cobble.setting.provider.SettingsReader;
import net.hasor.cobble.setting.provider.StreamType;
import net.hasor.cobble.setting.provider.properties.PropertiesSettingsReader;
import net.hasor.cobble.setting.provider.xml.XmlSettingsReader;
import net.hasor.cobble.setting.provider.yaml.YamlSettingsReader;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

/***
 * 传入{@link InputStream}的方式获取{@link Settings}接口的支持。
 * @version : 2013-9-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class MergedSettings extends BasicSettings implements IOSettings {
    private final LinkedList<ConfigSource> pendingConfigSource = new LinkedList<>();

    /**子类决定如何添加资源*/
    public MergedSettings() {
    }

    /** 将一个配置源添加到列表，后面会通过 load 方法加载这些数据。
     * 注意：待处理列表中的数据一旦装载完毕将会从待处理列表中清除出去。*/
    public boolean addResource(String mainSettings, StreamType type) {
        return this.addConfigSource(new ConfigSource(type, () -> {
            InputStream stream = ResourcesUtils.getResourceAsStream(mainSettings);
            return new InputStreamReader(stream, StandardCharsets.UTF_8);
        }));
    }

    /** 将一个配置源添加到列表，后面会通过 load 方法加载这些数据。
     * 注意：待处理列表中的数据一旦装载完毕将会从待处理列表中清除出去。*/
    public boolean addStream(InputStream mainSettings, StreamType type) {
        return this.addConfigSource(new ConfigSource(type, () -> new InputStreamReader(mainSettings, StandardCharsets.UTF_8)));
    }

    /** 将一个配置源添加到列表，后面会通过 load 方法加载这些数据。
     * 注意：待处理列表中的数据一旦装载完毕将会从待处理列表中清除出去。*/
    public boolean addStream(InputStream mainSettings, Charset charset, StreamType type) {
        return this.addConfigSource(new ConfigSource(type, () -> new InputStreamReader(mainSettings, charset)));
    }

    /** 将一个配置源添加到列表，后面会通过 load 方法加载这些数据。
     * 注意：待处理列表中的数据一旦装载完毕将会从待处理列表中清除出去。*/
    public boolean addStringBody(String mainSettings, StreamType type) {
        return this.addConfigSource(new ConfigSource(type, () -> new StringReader(mainSettings)));
    }

    /** 将一个配置源添加到列表，后面会通过 load 方法加载这些数据。
     * 注意：待处理列表中的数据一旦装载完毕将会从待处理列表中清除出去。*/
    public boolean addReader(Reader mainSettings, StreamType type) {
        return this.addConfigSource(new ConfigSource(type, () -> mainSettings));
    }

    /** 将一个配置源添加到列表，后面会通过 load 方法加载这些数据。
     * 注意：待处理列表中的数据一旦装载完毕将会从待处理列表中清除出去。*/
    protected boolean addConfigSource(ConfigSource configSource) {
        if (configSource == null || configSource.getStreamType() == null || configSource.getResourceReader() == null) {
            return false;
        }
        for (ConfigSource cs : this.pendingConfigSource) {
            if (cs.equals(configSource)) {
                return false;
            }
        }
        return this.pendingConfigSource.add(configSource);
    }

    /**load装载所有待处理的流，如果没有待处理流则直接return。*/
    @Override
    public int loadSettings() throws IOException {
        this.readyLoad();//准备装载
        int loadCount = 0;
        if (this.pendingConfigSource.isEmpty()) {
            return 0;
        }
        //构建装载环境
        ConfigSource entity = null;
        try {
            while ((entity = this.pendingConfigSource.removeFirst()) != null) {
                loadCount++;
                try {
                    // 拿到Reader
                    ResourceReader dataReader = entity.getResourceReader();
                    if (dataReader == null) {
                        continue;
                    }
                    SettingsReader settingsReader;
                    switch (entity.getStreamType()) {
                        case Xml:
                            settingsReader = new XmlSettingsReader();
                            break;
                        case Yaml:
                            settingsReader = new YamlSettingsReader();
                            break;
                        case Properties:
                            settingsReader = new PropertiesSettingsReader();
                            break;
                        default:
                            throw new UnsupportedOperationException(entity.getStreamType() + " Unsupported.");
                    }
                    settingsReader.readSetting(dataReader, this);
                } catch (Exception e) {
                    throw e;
                }
                if (this.pendingConfigSource.isEmpty()) {
                    break;
                }
            }
        } catch (Throwable e) {
            String errorMessage = "parsing failed -> ";
            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new IOException(errorMessage + e.getMessage(), e);
            }
        }
        this.loadFinish();//完成装载
        return loadCount;
    }

    /**准备装载*/
    protected void readyLoad() throws IOException {
        //
    }

    /**完成装载*/
    protected void loadFinish() throws IOException {
        //
    }
}
