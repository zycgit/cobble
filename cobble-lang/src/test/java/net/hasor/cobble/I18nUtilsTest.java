package net.hasor.cobble;
import org.junit.Test;

import java.io.IOException;

public class I18nUtilsTest {
    @Test
    public void testI18n() throws IOException {
        I18nUtils i18n = I18nUtils.initI18n("i18n/authlabel");
        i18n.addVariables("MY_NAME", "zyc");

        assert i18n.getMessage("abc", null, "zh_CN").equals("message form zh_cn {0} zyc");
        assert i18n.getMessage("abc", null, "en_US").equals("message form en_us {0} zyc");
        assert i18n.getMessage("abc", new Object[] { 123 }, "zh_CN").equals("message form zh_cn 123 zyc");
        assert i18n.getMessage("abc", new Object[] { 123 }, "en_US").equals("message form en_us 123 zyc");
    }
}
