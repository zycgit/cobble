/*
 * Copyright 2008-2026 the original author or authors.
 * Licensed under the Apache License, Version 2.0.
 */
package net.hasor.cobble;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import org.junit.Test;
import static org.junit.Assert.*;

/** Official Unicode data, with this utility's documented DNS name policy. */
public class IdnUnicodeDataTest {
    private static final Pattern ESCAPE = Pattern.compile("\\\\u([0-9a-fA-F]{4})|\\\\x\\{([0-9a-fA-F]+)\\}");

    @Test
    public void officialNontransitionalIdnaVectorsInBothDirections() throws Exception {
        int count = 0, rootNames = 0;
        try (BufferedReader reader = resource("IdnaTestV2.txt.gz")) {
            String line;
            while ((line = reader.readLine()) != null) {
                String body = line.split("#", 2)[0].trim();
                if (body.isEmpty()) {
                    continue;
                }
                String[] fields = Arrays.stream(body.split(";", -1)).map(String::trim).toArray(String[]::new);
                String source = unescape(fields[0]);
                String unicode = fields[1].isEmpty() ? source : unescape(fields[1]);
                String unicodeStatus = fields[2].isEmpty() ? "[]" : fields[2];
                String ascii = fields[3].isEmpty() ? unicode : unescape(fields[3]);
                String status = fields[4].isEmpty() ? unicodeStatus : fields[4];
                boolean error = !status.equals("[]");
                // UTS #46 VerifyDnsLength rejects an empty root label. Our DNS
                // API explicitly preserves it, while validating every other label.
                if (status.equals("[A4_2]") && validRootedAscii(ascii)) {
                    error = false;
                    rootNames++;
                }
                if (error) {
                    assertThrows("ASCII: " + body, IllegalArgumentException.class, () -> NetworkUtils.toDNSAscii(source));
                    assertThrows("Unicode: " + body, IllegalArgumentException.class, () -> NetworkUtils.toDNSUnicode(source));
                } else {
                    assertEquals(body, ascii, NetworkUtils.toDNSAscii(source));
                    assertEquals(body, unicode, NetworkUtils.toDNSUnicode(source));
                }
                count++;
            }
        }
        assertEquals(6391, count);
        assertEquals(68, rootNames);
        System.out.println("Unicode 17 IDNA vectors=" + count + ", preserved root names=" + rootNames);
    }

    @Test
    public void officialNfcNormalizationVectors() throws Exception {
        int rows = 0;
        try (BufferedReader reader = resource("NormalizationTest.txt.gz")) {
            String line;
            while ((line = reader.readLine()) != null) {
                String body = line.split("#", 2)[0].trim();
                if (body.isEmpty() || body.startsWith("@")) {
                    continue;
                }
                String[] fields = body.split(";", -1);
                String[] values = new String[5];
                for (int i = 0; i < values.length; i++) {
                    StringBuilder value = new StringBuilder();
                    for (String cp : fields[i].trim().split(" +")) {
                        value.appendCodePoint(Integer.parseInt(cp, 16));
                    }
                    values[i] = value.toString();
                }
                for (int i = 0; i < values.length; i++) {
                    assertEquals(body, values[i < 3 ? 1 : 3], IdnCodec.normalizeNfc(values[i]));
                }
                rows++;
            }
        }
        assertEquals(20034, rows);
    }

    private static boolean validRootedAscii(String name) {
        if (!name.endsWith(".") || name.length() < 2 || name.length() > 254) {
            return false;
        }
        for (String label : name.substring(0, name.length() - 1).split("\\.", -1)) {
            if (label.isEmpty() || label.length() > 63) {
                return false;
            }
        }
        return true;
    }

    private static String unescape(String value) {
        if (value.equals("\"\"")) {
            return "";
        }
        Matcher matcher = ESCAPE.matcher(value);
        StringBuilder decoded = new StringBuilder();
        int offset = 0;
        while (matcher.find()) {
            decoded.append(value, offset, matcher.start());
            String hex = matcher.group(1) == null ? matcher.group(2) : matcher.group(1);
            decoded.appendCodePoint(Integer.parseInt(hex, 16));
            offset = matcher.end();
        }
        return decoded.append(value, offset, value.length()).toString();
    }

    private static BufferedReader resource(String name) throws Exception {
        InputStream input = IdnUnicodeDataTest.class.getResourceAsStream("idna/" + name);
        assertNotNull(name, input);
        return new BufferedReader(new InputStreamReader(new GZIPInputStream(input), StandardCharsets.UTF_8));
    }
}
