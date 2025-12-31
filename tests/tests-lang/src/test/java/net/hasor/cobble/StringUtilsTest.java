package net.hasor.cobble;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void containsDotTest() {
        Assert.assertFalse(StringUtils.contains("1", "."));
        Assert.assertTrue(StringUtils.contains("abc.def", "."));
        Assert.assertFalse(StringUtils.contains("abcdef", "."));
        Assert.assertTrue(StringUtils.contains(".", "."));
        Assert.assertFalse(StringUtils.contains("", "."));
        Assert.assertFalse(StringUtils.contains(null, "."));
    }

    @Test
    public void startsWithDotTest() {
        Assert.assertTrue(StringUtils.startsWith(".abc", "."));
        Assert.assertFalse(StringUtils.startsWith("abc", "."));
        Assert.assertTrue(StringUtils.startsWith(".", "."));
    }

    @Test
    public void endsWithDotTest() {
        Assert.assertTrue(StringUtils.endsWith("abc.", "."));
        Assert.assertFalse(StringUtils.endsWith("abc", "."));
        Assert.assertTrue(StringUtils.endsWith(".", "."));
    }

    @Test
    public void containsIgnoreCaseDotTest() {
        Assert.assertTrue(StringUtils.containsIgnoreCase("abc.def", "."));
        Assert.assertTrue(StringUtils.containsIgnoreCase("ABC.DEF", "."));
    }

    @Test
    public void dotHandlingTest() {
        // contains
        Assert.assertTrue(StringUtils.contains("a.b", "."));
        Assert.assertFalse(StringUtils.contains("ab", "."));

        // startsWith
        Assert.assertTrue(StringUtils.startsWith(".a", "."));
        Assert.assertFalse(StringUtils.startsWith("a.", "."));

        // endsWith
        Assert.assertTrue(StringUtils.endsWith("a.", "."));
        Assert.assertFalse(StringUtils.endsWith(".a", "."));

        // containsIgnoreCase
        Assert.assertTrue(StringUtils.containsIgnoreCase("A.B", "."));

        // startsWithIgnoreCase
        Assert.assertTrue(StringUtils.startsWithIgnoreCase(".A", "."));

        // endsWithIgnoreCase
        Assert.assertTrue(StringUtils.endsWithIgnoreCase("A.", "."));
    }

    @Test
    public void dotHandlingAnyNoneTest() {
        Assert.assertTrue(StringUtils.containsAny("a.b", "."));
        Assert.assertFalse(StringUtils.containsNone("a.b", "."));
        Assert.assertTrue(StringUtils.containsOnly("...", "."));
    }
}
