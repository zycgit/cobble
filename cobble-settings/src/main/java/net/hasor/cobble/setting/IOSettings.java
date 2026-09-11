/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.setting;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import net.hasor.cobble.setting.provider.StreamType;

/**
 * 需要经过解析读取配置文件资源的{@link  Settings}
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2013-9-9
 */
public interface IOSettings extends Settings {
    void loadResource(String mainSettings, StreamType type) throws IOException;

    void loadStream(InputStream mainSettings, StreamType type) throws IOException;

    void loadStream(InputStream mainSettings, Charset charset, StreamType type) throws IOException;

    void loadStringBody(String mainSettings, StreamType type) throws IOException;

    void loadReader(Reader mainSettings, StreamType type) throws IOException;
}
