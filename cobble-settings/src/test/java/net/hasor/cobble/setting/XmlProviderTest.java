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
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.hasor.cobble.ResourcesUtils;
import net.hasor.cobble.setting.provider.StreamType;
import net.hasor.cobble.setting.provider.xml.XmlSettingsReader;
import org.junit.Test;

public class XmlProviderTest {
    @Test
    public void xmlTest_1() throws IOException {
        InputStream conf = ResourcesUtils.getResourceAsStream("/net_hasor_core_settings/simple-config.xml");
        InputStreamReader settingReader = new InputStreamReader(conf, StandardCharsets.UTF_8);

        XmlSettingsReader reader = new XmlSettingsReader();
        BasicSettings settings = new BasicSettings();
        reader.readSetting(settingReader, settings);

        assert settings.getString("mySelf.myName").equals("赵永春");
        assert settings.getString("myself.myname") == null;
        assert settings.getInteger("mySelf.myAge") == 12;
        assert settings.getString("mySelf.myBirthday").equals("1986-01-01 00:00:00");
        assert settings.getString("mySelf.myWork").equals("Software Engineer");
        assert settings.getString("mySelf.myProjectURL").equals("http://www.hasor.net/");
        assert settings.getString("mySelf.source").equals("Xml");
    }

    @Test
    public void xmlTest_2() throws Exception {
        String data = //
                "<?xml version='1.0' encoding='UTF-8'?>\n" +//
                "<config xmlns='http://www.hasor.net/sechma/main'>\n" +//
                "    <hasor debug='false'>\n" + //
                "        <debug>true</debug>\n" +//
                "    </hasor>\n" +//
                "</config>";

        MergedSettings settings = new MergedSettings();
        settings.loadStringBody(data, StreamType.Xml);

        Boolean aBoolean = settings.getBoolean("hasor.debug");
        Boolean[] aBooleanArray = settings.getBooleanArray("hasor.debug");
        assert aBoolean;
        assert aBooleanArray.length == 2;
        assert !aBooleanArray[0];
        assert aBooleanArray[1];
    }

    @Test
    public void xmlTest_3() throws Exception {
        MergedSettings settings = new MergedSettings();
        settings.loadResource("classpath:/net_hasor_core_settings/ns1-config.xml", StreamType.Xml);
        settings.loadResource("classpath:/net_hasor_core_settings/ns2-config.xml", StreamType.Xml);

        Settings ns1 = settings.getSettings("http://mode1.myProject.net");
        Settings ns2 = settings.getSettings("http://mode2.myProject.net");

        assert ns1.getString("appSettings.serverLocal.url").equals("www.126.com");
        assert ns2.getString("appSettings.serverLocal.url").equals("www.souhu.com");
    }

    @Test
    public void xmlTest_4() throws Exception {
        MergedSettings settings = new MergedSettings();
        settings.loadResource("classpath:/net_hasor_core_settings/ns-all-config.xml", StreamType.Xml);

        Settings ns1 = settings.getSettings("http://mode1.myProject.net");
        Settings ns2 = settings.getSettings("http://mode2.myProject.net");

        assert ns1.getString("appSettings.serverLocal.url").equals("www.126.com");
        assert ns2.getString("appSettings.serverLocal.url").equals("www.souhu.com");
    }

    @Test
    public void xmlTest_5() throws Exception {
        MergedSettings settings = new MergedSettings();
        settings.loadResource("classpath:/net_hasor_core_settings/main-ns-config.xml", StreamType.Xml);

        assert settings.getString("appSettings.serverLocal.url").equals("www.google.com");
    }
}
