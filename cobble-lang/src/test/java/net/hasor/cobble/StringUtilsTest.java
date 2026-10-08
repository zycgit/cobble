/*
 * Copyright 2008-2026 the original author or authors.
 * Licensed under the Apache License, Version 2.0.
 */
package net.hasor.cobble;
import java.util.Locale;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringUtilsTest {
    @Test
    public void equalsIgnoreCaseHandlesNullAndEmptyStrings() {
        assertTrue(StringUtils.equalsIgnoreCase(null, null));
        assertFalse(StringUtils.equalsIgnoreCase(null, ""));
        assertFalse(StringUtils.equalsIgnoreCase("", null));
        assertTrue(StringUtils.equalsIgnoreCase("", ""));
        assertTrue(StringUtils.equalsIgnoreCase("HtTpS", "https"));
        assertFalse(StringUtils.equalsIgnoreCase("https", "http"));
    }

    @Test
    public void equalsIgnoreCaseDoesNotDependOnDefaultLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertTrue(StringUtils.equalsIgnoreCase("CONNECTION", "connection"));
            assertTrue(StringUtils.equalsIgnoreCase("DIRECT", "direct"));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void equalsIgnoreCaseUsesCharacterComparisonWithoutExpandingCase() {
        assertFalse(StringUtils.equalsIgnoreCase("straße", "STRASSE"));
        assertFalse(StringUtils.equalsIgnoreCase("\uFB03", "FFI"));
        assertTrue(StringUtils.equalsIgnoreCase("\u0130", "i"));
        assertTrue(StringUtils.equalsIgnoreCase("\u03C2", "\u03C3"));
    }
}
