package net.hasor.cobble.reflect.annotations;

import net.hasor.cobble.reflect.Annotations;
import org.junit.Test;

import java.io.IOException;

public class BasicTest {
    @Test
    public void test() throws IOException {
        Annotations annotations = Annotations.ofClass(TestBean.class);
        assert annotations.getAnnotation(ResultMap.class) != null;
    }

    @ResultMap()
    class TestBean {
    }
}