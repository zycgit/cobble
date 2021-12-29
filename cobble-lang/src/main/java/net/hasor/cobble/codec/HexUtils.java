/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.cobble.codec;

/**
 *
 * @version : 2012-6-21
 * @author 赵永春 (zyc@hasor.net)
 */
public class HexUtils {

    /** bytes转换成十六进制字符串 */
    public static String bytes2hex(final byte[] b) {
        String hs = "";
        String stmp = "";
        for (byte element : b) {
            stmp = Integer.toHexString(element & 0XFF);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }

    /** hexString to bytes */
    public static byte[] hex2bytes(String src) {
        if (src.length() % 2 == 1) {
            src = "0" + src;
        }

        int count = src.length() / 2;
        byte[] ret = new byte[count];
        for (int i = 0; i < count; i++) {
            int index = i * 2;
            char c1 = src.charAt(index);
            char c2 = src.charAt(index + 1);
            ret[i] = (byte) (toByte(c1) << 4);
            ret[i] = (byte) (ret[i] | toByte(c2));
        }
        return ret;
    }

    private static byte toByte(char src) {
        switch (Character.toUpperCase(src)) {
            case '0':
                return 0x0;
            case '1':
                return 0x1;
            case '2':
                return 0x2;
            case '3':
                return 0x3;
            case '4':
                return 0x4;
            case '5':
                return 0x5;
            case '6':
                return 0x6;
            case '7':
                return 0x7;
            case '8':
                return 0x8;
            case '9':
                return 0x9;
            case 'A':
                return 0xa;
            case 'B':
                return 0xb;
            case 'C':
                return 0xc;
            case 'D':
                return 0xd;
            case 'E':
                return 0xe;
            case 'F':
                return 0xf;
            default:
                throw new IllegalStateException("0-F");
        }
    }
}
