/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.codec;
import java.nio.charset.StandardCharsets;

/**
 * 简单编码类，该类提供了Base64的编码，该编码器与配套的js编码器可以实现互相编码互相转化功能。
 * @author 赵永春 (zyc@hasor.net)
 * @version 2009-4-28
 */
public abstract class Base64 {
    /**
     * Standard Base64 encoding as described in the Section 3 of
     * <a href="http://www.faqs.org/rfcs/rfc3548.html">RFC3548</a>.
     */
    protected static final String STANDARD = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"; // supplement
    /**
     * Base64-like encoding that is URL-safe as described in the Section 4 of
     * <a href="http://www.faqs.org/rfcs/rfc3548.html">RFC3548</a>.  It is
     * important to note that data encoded this way is <em>not</em> officially
     * valid Base64, or at the very least should not be called Base64 without
     * also specifying that is was encoded using the URL-safe dialect.
     */
    protected static final String URL_SAFE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"; // supplement
    /**
     * Special "ordered" dialect of Base64 described in
     * <a href="http://www.faqs.org/qa/rfcc-1940.html">RFC1940</a>.
     */
    protected static final String ORDERED  = "-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz"; // supplement

    /**
     * 使用UTF-8编码进行Base64编码
     * @param s 要编码的原始数据
     * @return 返回编码之后的字符串。
     */
    public static String base64Encode(final String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        byte[] b = null;
        b = s.getBytes(StandardCharsets.UTF_8);
        return Base64.base64EncodeFoArray(b);
    }

    /** Encoding a byte array to a string follow the Base64 regular. */
    public static String base64EncodeFoArray(final byte[] s) {
        if (s == null) {
            return null;
        }
        if (s.length == 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        int b0, b1, b2, b3;
        int len = s.length;
        int i = 0;
        while (i < len) {
            byte tmp = s[i++];
            b0 = (tmp & 0xfc) >> 2;
            b1 = (tmp & 0x03) << 4;
            if (i < len) {
                tmp = s[i++];
                b1 |= (tmp & 0xf0) >> 4;
                b2 = (tmp & 0x0f) << 2;
                if (i < len) {
                    tmp = s[i++];
                    b2 |= (tmp & 0xc0) >> 6;
                    b3 = tmp & 0x3f;
                } else {
                    b3 = 64; // 1 byte "-" is supplement
                }
            } else {
                b2 = b3 = 64;// 2 bytes "-" are supplement
            }
            buf.append(Base64.STANDARD.charAt(b0));
            buf.append(Base64.STANDARD.charAt(b1));
            buf.append(Base64.STANDARD.charAt(b2));
            buf.append(Base64.STANDARD.charAt(b3));
        }
        return buf.toString();
    }

    /** Decoding a string to a string follow the Base64 regular. */
    public static String base64Decode(final String s) {
        byte[] b = Base64.base64DecodeToArray(s);
        if (b == null) {
            return null;
        }
        if (b.length == 0) {
            return "";
        }
        return new String(b, StandardCharsets.UTF_8);
    }

    /** Decoding a string to a byte array follow the Base64 regular */
    public static byte[] base64DecodeToArray(final String s) {
        if (s == null) {
            return null;
        }
        int len = s.length();
        if (len == 0) {
            return new byte[0];
        }
        if (len % 4 != 0) {
            throw new IllegalArgumentException(s);
        }
        byte[] b = new byte[len / 4 * 3];
        int i = 0, j = 0, e = 0, c, tmp;
        while (i < len) {
            c = Base64.STANDARD.indexOf(s.charAt(i++));
            tmp = c << 18;
            c = Base64.STANDARD.indexOf(s.charAt(i++));
            tmp |= c << 12;
            c = Base64.STANDARD.indexOf(s.charAt(i++));
            if (c < 64) {
                tmp |= c << 6;
                c = Base64.STANDARD.indexOf(s.charAt(i++));
                if (c < 64) {
                    tmp |= c;
                } else {
                    e = 1;
                }
            } else {
                e = 2;
                i++;
            }
            b[j + 2] = (byte) (tmp & 0xff);
            tmp >>= 8;
            b[j + 1] = (byte) (tmp & 0xff);
            tmp >>= 8;
            b[j] = (byte) (tmp & 0xff);
            j += 3;
        }
        if (e != 0) {
            len = b.length - e;
            byte[] copy = new byte[len];
            System.arraycopy(b, 0, copy, 0, len);
            return copy;
        }
        return b;
    }
}
