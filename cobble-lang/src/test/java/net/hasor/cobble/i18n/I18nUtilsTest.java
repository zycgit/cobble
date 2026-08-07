package net.hasor.cobble.i18n;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;

public class I18nUtilsTest {
    @Test
    public void testI18n() throws IOException {
        I18nUtils i18n = I18nUtils.initI18n("i18n/authlabel");
        i18n.addVariables("MY_NAME", "zyc");

        assert i18n.getMessage("abc", null, "zh_CN").equals("message form zh_cn {0} zyc");
        assert i18n.getMessage("abc", null, "en_US").equals("message form en_us {0} zyc");
        assert i18n.getMessage("abc", new Object[] { 123 }, "zh_CN").equals("message form zh_cn 123 zyc");
        assert i18n.getMessage("abc", new Object[] { 123 }, "en_US").equals("message form en_us 123 zyc");
        Assert.assertEquals("missing", i18n.getMessage("missing", null, (Locale) null));
    }

    @Test
    public void loadResourcesDoesNotModifySourcesWhileGetMessageIterates() throws Exception {
        CountDownLatch localeLookupStarted = new CountDownLatch(1);
        CountDownLatch continueLocaleLookup = new CountDownLatch(1);
        AtomicBoolean blockLocaleLookup = new AtomicBoolean(true);
        ClassLoader classLoader = new ClassLoader(I18nUtilsTest.class.getClassLoader()) {
            @Override
            public URL getResource(String name) {
                if ("i18n/authlabel_zh_CN.properties".equals(name) && blockLocaleLookup.compareAndSet(true, false)) {
                    localeLookupStarted.countDown();
                    try {
                        if (!continueLocaleLookup.await(5, TimeUnit.SECONDS)) {
                            throw new IllegalStateException("Timed out waiting to continue locale lookup.");
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException(e);
                    }
                }
                return super.getResource(name);
            }
        };

        I18nUtils i18n = I18nUtils.initI18n(classLoader, "i18n/authlabel");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch resourceLoadStarted = new CountDownLatch(1);
        CountDownLatch resourceLoadFinished = new CountDownLatch(1);
        try {
            Future<String> message = executor.submit(() -> i18n.getMessage("abc", null, "zh_CN"));
            Assert.assertTrue(localeLookupStarted.await(5, TimeUnit.SECONDS));

            Future<?> resourceLoad = executor.submit(() -> {
                resourceLoadStarted.countDown();
                try {
                    i18n.loadResources(classLoader, "i18n/concurrent");
                    return null;
                } finally {
                    resourceLoadFinished.countDown();
                }
            });
            Assert.assertTrue(resourceLoadStarted.await(5, TimeUnit.SECONDS));
            Assert.assertFalse(resourceLoadFinished.await(200, TimeUnit.MILLISECONDS));

            continueLocaleLookup.countDown();
            Assert.assertEquals("message form zh_cn {0} MY_NAME", message.get(5, TimeUnit.SECONDS));
            resourceLoad.get(5, TimeUnit.SECONDS);
            Assert.assertEquals("loaded concurrently", i18n.getMessage("concurrent", null, "zh_CN"));
        } finally {
            continueLocaleLookup.countDown();
            executor.shutdownNow();
        }
    }
}
