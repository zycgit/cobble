/*
 * Copyright 2008-2026 the original author or authors.
 * Licensed under the Apache License, Version 2.0.
 */
package net.hasor.cobble;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class NetworkUtilsDnsTest {
    @Test
    public void convertsBothDirections() {
        String[][] names = {//
                { "中国.中国", "xn--fiqs8s.xn--fiqs8s" },    //
                { "bücher.de", "xn--bcher-kva.de" },        //
                { "hoß.de", "xn--ho-hia.de" },              //
                { "straße.de", "xn--strae-oqa.de" },        //
                { "ς.example", "xn--3xa.example" },         //
                { "نامه‌ای.com", "xn--mgba3gch31f060k.com" },//
                { "ශ්‍රී.com", "xn--10cl1a0b660p.com" } };
        for (String[] pair : names) {
            assertEquals(pair[1], NetworkUtils.toDNSAscii(pair[0]));
            assertEquals(pair[0], NetworkUtils.toDNSUnicode(pair[1]));
            assertEquals(pair[1], NetworkUtils.toDNSAscii(pair[1]));
            assertEquals(pair[0], NetworkUtils.toDNSUnicode(pair[0]));
        }
    }

    @Test
    public void normalizesCaseSeparatorsAndCanonicalEquivalents() {
        assertEquals("example.com.", NetworkUtils.toDNSAscii("EXAMPLE.COM."));
        assertEquals("example.com.", NetworkUtils.toDNSUnicode("EXAMPLE.COM."));
        assertEquals("bücher.de.", NetworkUtils.toDNSUnicode("BÜCHER。ＤＥ｡"));
        assertEquals("xn--bcher-kva.de.", NetworkUtils.toDNSAscii("BU\u0308CHER。ＤＥ｡"));
        assertEquals("hoß.de", NetworkUtils.toDNSUnicode("XN--HO-HIA.DE"));
        assertEquals("xn--o39a.example", NetworkUtils.toDNSAscii("\u1100\u1161.example"));
        assertEquals("가.example", NetworkUtils.toDNSUnicode("xn--o39a.example"));
    }

    @Test
    public void validatesAlabelsRatherThanPassingInvalidInputThrough() {
        for (String name : new String[] { "xn--", "xn--a.example", "xn--abc-.example", "xn--0.example",//
                "xn--u-ccb.example", "xn--é.example", "xn--zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz" }) {
            rejects(name);
        }
    }

    @Test
    public void rejectsMalformedNamesAndUtf16BeforeMapping() {
        for (String name : new String[] { null, "", ".", "..", "a..example", "example..", "-a.example", "a-.example",   //
                "ab--c.example", "_a.example", "a/b.example", "a:b.example", "*.example", "https://example.com",        //
                "a\u0000.example", "\uff3fa.example", "\ud800.example", "\udc00.example", "\ud800\u00ad\udc00.example", //
                "\u0301a.example", "\u00ad", "\u00ad.example" }) {
            rejects(name);
        }
    }

    @Test
    public void checksBidiAndContextRules() {
        assertEquals("l·l.cat", NetworkUtils.toDNSUnicode("xn--ll-0ea.cat"));
        assertEquals("xn--ll-0ea.cat", NetworkUtils.toDNSAscii("l·l.cat"));
        for (String name : new String[] { "a·b.cat", "a\u200cb.example", "a\u200db.example", "a\u05d0.example",//
                "0a.\u05d0", "\u0375a.example", "a\u05f3.example", "a\u30fbb.example", "\u0627\u0660\u06f1.example" }) {
            rejects(name);
        }
    }

    @Test
    public void countsHyphenPositionsAsUnicodeCodePoints() {
        rejects("𠮷a--b.example");
        rejects("xn--a--b-zy06c.example");
        assertEquals("xn----ab-zy06c.example", NetworkUtils.toDNSAscii("𠮷--ab.example"));
        assertEquals("𠮷--ab.example", NetworkUtils.toDNSUnicode("xn----ab-zy06c.example"));
    }

    @Test
    public void appliesDnsLengthLimitsInBothDirections() {
        String maximum = ("a".repeat(63) + ".").repeat(3) + "a".repeat(61);
        assertEquals(maximum, NetworkUtils.toDNSAscii(maximum));
        assertEquals(maximum + ".", NetworkUtils.toDNSUnicode(maximum + "."));
        rejects(maximum + "a");
        rejects("a".repeat(64) + ".example");
        rejects("ü".repeat(64) + ".example");
    }

    @Test(timeout = 10000)
    public void processesLongMappedInputWithoutQuadraticNormalization() {
        assertEquals("xn--fiqs8s", NetworkUtils.toDNSAscii("\u00ad".repeat(100000) + "中国"));
        rejects("a" + "\u0315\u0300".repeat(50000) + ".example");
        rejects("xn--" + "a".repeat(100000));
    }

    private static void rejects(String name) {
        assertThrows(String.valueOf(name), IllegalArgumentException.class, () -> NetworkUtils.toDNSAscii(name));
        assertThrows(String.valueOf(name), IllegalArgumentException.class, () -> NetworkUtils.toDNSUnicode(name));
    }
}
