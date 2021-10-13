package net.hasor.cobble.loader.isol;
import net.hasor.cobble.ResourcesUtils;
import net.hasor.cobble.loader.ResourceLoader;
import net.hasor.cobble.loader.providers.PathResourceLoader;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class IsolResourceLoaderTests {
    private IsolResourceLoader loader;

    @Before
    public void beforeImport() throws IOException {
        String dir1 = ResourcesUtils.getResource("/dir/isol/app-1").getFile();
        PathResourceLoader loader1 = new PathResourceLoader(new File(dir1));
        String dir2 = ResourcesUtils.getResource("/dir/isol/app-2").getFile();
        PathResourceLoader loader2 = new PathResourceLoader(new File(dir2));

        this.loader = new IsolResourceLoader();
        this.loader.bindParentLoader(Thread.currentThread().getContextClassLoader());
        this.loader.addImport("ApplicationForApp1.class", loader1);
        this.loader.addImport("ApplicationForApp2.class", loader2);
    }

    @Test
    public void isolLoaderTest1() throws Exception {
        ClassLoader classLoaderRoot = this.loader.toClassLoader();
        Object obj1 = classLoaderRoot.loadClass("ApplicationForApp1").newInstance();
        Object obj2 = classLoaderRoot.loadClass("ApplicationForApp2").newInstance();

        Object result1 = ((Function) obj1).apply("hello");
        Object result2 = ((Function) obj2).apply("hello");

        assert result1.toString().equals("FooForApp1 running...");
        assert result2.toString().equals("FooForApp2 running...");
    }

    @Test
    public void isolLoaderTest2() throws Exception {
        List<Object> list1 = loader.scanResources(ResourceLoader.ScanEvent::getName);
        assert list1.size() == 2;

        List<Object> list2 = loader.scanResources(ResourceLoader.MatchType.Suffix, ResourceLoader.ScanEvent::getName, new String[] { "App1.class" });
        assert list2.contains("ApplicationForApp1.class");
    }
}
