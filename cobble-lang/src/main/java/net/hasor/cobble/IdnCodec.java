/*
 * Copyright 2008-2026 the original author or authors.
 * Licensed under the Apache License, Version 2.0.
 */
package net.hasor.cobble;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;
import net.hasor.cobble.provider.SingleProvider;

/** Package-private IDNA conversion shared by the DNS methods in NetworkUtils. */
final class IdnCodec {
    // ==================== Unicode 属性与编码常量 ====================
    // 属性编号与内置 Unicode 数据的编码布局一致，仅保留 IDNA 所需的类别。
    private static final int VALID = 0, IGNORED = 1, MAPPED = 2, DEVIATION = 3;
    private static final int L = 0, R = 1, AL = 2, EN = 3, ES = 4, CS = 5, ET = 6, ON = 7, BN = 8, NSM = 9, AN = 10;
    private static final int JOIN_LEFT = 1, JOIN_RIGHT = 2, JOIN_DUAL = 3, JOIN_TRANSPARENT = 4;
    private static final int GREEK = 1, HEBREW = 2, HIRAGANA = 3, KATAKANA = 4, HAN = 5;
    private static final int    LTR_MASK        = (1 << L) | (1 << EN) | (1 << ES) | (1 << CS) | (1 << ET) | (1 << ON) | (1 << BN) | (1 << NSM);
    private static final int    RTL_MASK        = (LTR_MASK & ~(1 << L)) | (1 << R) | (1 << AL) | (1 << AN);
    private static final String PUNYCODE_DIGITS = "abcdefghijklmnopqrstuvwxyz0123456789";

    // ==================== Unicode 数据与延迟加载 ====================
    // 普通 ASCII 名称无需数据表；处理 Unicode 或 A-label 时才通过 SingleProvider 加载。
    private static final SingleProvider<IdnCodec> INSTANCE       = new SingleProvider<>(IdnCodec::new);
    // 分段表记录区间起始码点，同一区间内共享映射状态或字符属性。
    private final        int[]                    mappingStarts;
    private final        byte[]                   statuses;
    private final        String[]                 mappings;
    private final        int[]                    propertyStarts;
    private final        int[]                    properties;
    private final        Map<Integer, int[]>      decompositions = new HashMap<>();
    private final        Map<Long, Integer>       compositions   = new HashMap<>();

    private final Comparator<Integer> combiningOrder = Comparator.comparingInt(this::combiningClass);

    // ==================== 读取内置 Unicode 数据 ====================
    private IdnCodec() {
        try (InputStream raw = IdnCodec.class.getResourceAsStream("idna/unicode-17.0.0.bin.gz")) {
            if (raw == null) {
                throw new IOException("Missing IDNA Unicode data");
            }

            try (DataInputStream input = new DataInputStream(new GZIPInputStream(raw))) {
                // ---------- 文件头：魔数与 Unicode 主版本 ----------
                if (input.readInt() != 0x49444e41 || input.readInt() != 17) {
                    throw new IOException("Invalid IDNA Unicode data version");
                }

                // ---------- UTS #46 映射区间：起点、状态与替换序列 ----------
                int count = input.readInt();
                mappingStarts = new int[count];
                statuses = new byte[count];
                mappings = new String[count];
                for (int i = 0; i < count; i++) {
                    mappingStarts[i] = input.readInt();
                    statuses[i] = input.readByte();
                    StringBuilder text = new StringBuilder();
                    int length = input.readUnsignedByte();
                    for (int j = 0; j < length; j++) {
                        text.appendCodePoint(input.readInt());
                    }
                    mappings[i] = text.toString();
                }

                // ---------- 字符属性区间：组合类、Mark、Bidi、连接类型与脚本 ----------
                count = input.readInt();
                propertyStarts = new int[count];
                properties = new int[count];
                for (int i = 0; i < count; i++) {
                    propertyStarts[i] = input.readInt();
                    properties[i] = input.readInt();
                }

                // ---------- NFC 规范分解表：单个码点对应的分解序列 ----------
                count = input.readInt();
                for (int i = 0; i < count; i++) {
                    int cp = input.readInt();
                    int[] sequence = new int[input.readUnsignedByte()];
                    for (int j = 0; j < sequence.length; j++) {
                        sequence[j] = input.readInt();
                    }
                    decompositions.put(cp, sequence);
                }

                // ---------- NFC 合成表：两个码点对应的合成结果 ----------
                // 每个码点占 21 位，两个码点拼成 long 键，查询时使用相同布局。
                count = input.readInt();
                for (int i = 0; i < count; i++) {
                    compositions.put(((long) input.readInt() << 21) | input.readInt(), input.readInt());
                }
                // 所有表读完后必须到达文件结尾，拒绝布局不匹配的尾随数据。
                if (input.read() != -1) {
                    throw new IOException("Trailing IDNA Unicode data");
                }
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    // ==================== 域名转换主流程 ====================
    static String convert(String name, boolean toAscii) {
        // ---------- 1. 参数检查与普通 ASCII 快速路径 ----------
        // 含 xn-- 的名称仍须解码并校验，不能只按 ASCII 字符检查后返回。
        require(!StringUtils.isEmpty(name), "empty name");
        if (isAscii(name)) {
            String ascii = StringUtils.lowerCase(name, Locale.ROOT);
            if (!ascii.startsWith("xn--") && !ascii.contains(".xn--")) {
                int end = ascii.endsWith(".") ? ascii.length() - 1 : ascii.length();
                require(end > 0 && end <= 253, "name length");
                int start = 0;
                for (int i = 0; i <= end; i++) {
                    if (i == end || ascii.charAt(i) == '.') {
                        int length = i - start;
                        require(length > 0 && length <= 63, "label length");
                        require(ascii.charAt(start) != '-' && ascii.charAt(i - 1) != '-', "edge hyphen");
                        require(length < 4 || ascii.charAt(start + 2) != '-' || ascii.charAt(start + 3) != '-', "reserved hyphens");
                        start = i + 1;
                    } else {
                        require(isLdh(ascii.charAt(i)), "STD3 character");
                    }
                }
                return ascii;
            }
        }

        // ---------- 2. UTS #46 非过渡映射与 NFC 规范化 ----------
        IdnCodec codec = INSTANCE.get();
        StringBuilder mapped = new StringBuilder(name.length());
        for (int offset = 0; offset < name.length(); ) {
            int cp = name.codePointAt(offset);
            offset += Character.charCount(cp);
            // 必须先拒绝孤立代理项，避免删除可忽略字符后把两个孤立代理项拼成有效码点。
            require(cp < 0xd800 || cp > 0xdfff, "unpaired surrogate");
            int index = floor(codec.mappingStarts, cp);
            switch (codec.statuses[index]) {
                case IGNORED:
                    break;
                case MAPPED:
                    mapped.append(codec.mappings[index]);
                    break;
                default:
                    // 非过渡模式保留 deviation 字符；禁止字符留待规范化后的标签校验拒绝。
                    mapped.appendCodePoint(cp);
                    break;
            }
        }
        String normalized = normalizeNfc(mapped.toString());

        // ---------- 3. 分离根点并拆分标签 ----------
        // 保留末尾根点，同时保留拆分得到的空标签，以便拒绝连续分隔符。
        boolean rooted = normalized.endsWith(".");
        String body = rooted ? StringUtils.removeEnd(normalized, ".") : normalized;
        require(!StringUtils.isEmpty(body), "empty name");
        String[] labels = StringUtils.splitPreserveAllTokens(body, '.');
        // 最短标签占一个 ASCII 字节；加上标签间的点，253 字节最多容纳 127 个标签。
        require(labels.length <= 127, "name length");

        // ---------- 4. 解码并校验每个标签 ----------
        int[][] decoded = new int[labels.length][];
        boolean bidiDomain = false;
        for (int labelIndex = 0; labelIndex < labels.length; labelIndex++) {
            String label = labels[labelIndex];
            require(!StringUtils.isEmpty(label), "empty label");
            // 先限制解码输入规模；最终还需检查 Punycode 编码后的 63 字节上限。
            require(label.codePointCount(0, label.length()) <= 63, "label length");

            // ---------- 4.1 A-label 解码与回编码检查 ----------
            if (label.startsWith("xn--")) {
                require(isAscii(label), "non-ASCII A-label");
                String punycode = label.substring(4);
                List<Integer> output = new ArrayList<>();
                // 最后一个连字符前是基本码点，之后是非 ASCII 码点的增量编码。
                int delimiter = punycode.lastIndexOf('-'), cursor = 0, bias = 72;
                if (delimiter >= 0) {
                    for (int j = 0; j < delimiter; j++) {
                        require(punycode.charAt(j) < 128, "Punycode basic character");
                        output.add((int) punycode.charAt(j));
                    }
                    cursor = delimiter + 1;
                }
                // n 为当前码点，i 累计插入位置；用 long 计算并检查范围，避免整数回绕。
                long n = 128, i = 0;
                while (cursor < punycode.length()) {
                    long old = i, weight = 1;
                    for (int k = 36; ; k += 36) {
                        require(cursor < punycode.length(), "Punycode truncated");
                        char ch = punycode.charAt(cursor++);
                        int digit = ch >= 'a' && ch <= 'z' ? ch - 'a' : ch >= 'A' && ch <= 'Z' ? ch - 'A' : ch >= '0' && ch <= '9' ? ch - '0' + 26 : -1;
                        require(digit >= 0, "Punycode digit");
                        i += digit * weight;
                        require(i <= Integer.MAX_VALUE, "Punycode overflow");
                        int threshold = Math.min(26, Math.max(1, k - bias));
                        if (digit < threshold) {
                            break;
                        }
                        weight *= 36 - threshold;
                        require(weight <= Integer.MAX_VALUE, "Punycode overflow");
                    }
                    bias = adaptPunycodeBias(i - old, output.size() + 1, old == 0);
                    n += i / (output.size() + 1);
                    i %= output.size() + 1;
                    require(n <= 0x10ffff && (n < 0xd800 || n > 0xdfff), "Punycode scalar");
                    output.add((int) i, (int) n);
                    i++;
                }
                int[] points = output.stream().mapToInt(Integer::intValue).toArray();
                String unicode = new String(points, 0, points.length);
                // A-label 必须含非 ASCII 字符且已是 NFC，重新编码后必须得到相同载荷。
                require(!isAscii(unicode), "fake A-label");
                require(normalizeNfc(unicode).equals(unicode), "A-label is not NFC");
                require(encodePunycode(points).equals(label.substring(4)), "A-label round trip");
                label = unicode;
            }

            // ---------- 4.2 标签长度、连字符与首字符 ----------
            int[] points = label.codePoints().toArray();
            require(points.length > 0 && points.length <= 63, "label length");
            require(points[0] != '-' && points[points.length - 1] != '-', "edge hyphen");
            // 连字符位置按 Unicode 码点计数，补充平面字符也只占一个位置。
            require(points.length < 4 || points[2] != '-' || points[3] != '-', "reserved hyphens");
            require((codec.property(points[0]) & 256) == 0, "leading combining mark");

            // ---------- 4.3 逐码点有效性与上下文规则 ----------
            boolean japanese = false, arabic = false, extendedArabic = false, middleDot = false;
            for (int i = 0; i < points.length; i++) {
                int cp = points[i];
                int status = codec.statuses[floor(codec.mappingStarts, cp)];
                require(status == VALID || status == DEVIATION, "disallowed or unmapped character");
                require(cp >= 128 || isLdh(cp), "STD3 character");

                // ContextJ：前一字符为 virama 时允许两种连接符，否则须满足连接上下文。
                if ((cp == 0x200c || cp == 0x200d) && (i == 0 || codec.combiningClass(points[i - 1]) != 9)) {
                    require(points[i] == 0x200c, "ContextJ zero width joiner");
                    int left = i - 1, right = i + 1;
                    // 透明连接类型不参与左右连接关系的判定。
                    while (left >= 0 && codec.joining(points[left]) == JOIN_TRANSPARENT) {
                        left--;
                    }
                    while (right < points.length && codec.joining(points[right]) == JOIN_TRANSPARENT) {
                        right++;
                    }
                    require(left >= 0 && right < points.length, "ContextJ non-joiner context");
                    int before = codec.joining(points[left]), after = codec.joining(points[right]);
                    require((before == JOIN_LEFT || before == JOIN_DUAL) && (after == JOIN_RIGHT || after == JOIN_DUAL), "ContextJ non-joiner context");
                }

                // ContextO：检查特定标点的邻接字符，并记录整段标签的脚本、数字组。
                int script = codec.script(cp);
                japanese |= script == HIRAGANA || script == KATAKANA || script == HAN;
                arabic |= cp >= 0x660 && cp <= 0x669;
                extendedArabic |= cp >= 0x6f0 && cp <= 0x6f9;
                middleDot |= cp == 0x30fb;
                if (cp == 0xb7) {
                    require(i > 0 && i + 1 < points.length && points[i - 1] == 'l' && points[i + 1] == 'l', "ContextO middle dot");
                } else if (cp == 0x375) {
                    require(i + 1 < points.length && codec.script(points[i + 1]) == GREEK, "ContextO Greek");
                } else if (cp == 0x5f3 || cp == 0x5f4) {
                    require(i > 0 && codec.script(points[i - 1]) == HEBREW, "ContextO Hebrew");
                }
            }
            // 片假名中点与阿拉伯数字组的限制依赖整个标签，遍历结束后统一判断。
            require(!middleDot || japanese, "ContextO Katakana");
            require(!arabic || !extendedArabic, "ContextO digits");

            // ---------- 4.4 收集整个域名的 Bidi 状态 ----------
            // 任一标签含 R、AL 或 AN，后续都必须对域名中的所有标签应用 Bidi 规则。
            for (int cp : points) {
                int direction = codec.bidi(cp);
                bidiDomain |= direction == R || direction == AL || direction == AN;
            }
            decoded[labelIndex] = points;
        }

        // ---------- 5. 全域 Bidi 校验、DNS 长度检查与输出 ----------
        int asciiLength = labels.length - 1;
        StringBuilder result = new StringBuilder();
        for (int[] points : decoded) {
            // 一旦域名触发 Bidi 规则，本身只有 ASCII 的标签也需要校验。
            if (bidiDomain) {
                int first = codec.bidi(points[0]);
                require(first == L || first == R || first == AL, "Bidi first character");
                boolean rtl = first != L, en = false, an = false;
                int last = first;
                for (int cp : points) {
                    int direction = codec.bidi(cp);
                    int allowed = rtl ? RTL_MASK : LTR_MASK;
                    require((allowed & (1 << direction)) != 0, "Bidi character");
                    if (direction != NSM) {
                        last = direction;
                    }
                    en |= direction == EN;
                    an |= direction == AN;
                }
                require(rtl ? last == R || last == AL || last == EN || last == AN : last == L || last == EN, "Bidi last character");
                require(!rtl || !en || !an, "Bidi mixed numbers");
            }

            // 两个转换方向都按 ASCII/Punycode 结果校验长度；253 字节不包含末尾根点。
            String unicode = new String(points, 0, points.length);
            String ascii = isAscii(unicode) ? unicode : "xn--" + encodePunycode(points);
            require(ascii.length() <= 63, "label length");
            asciiLength += ascii.length();
            require(asciiLength <= 253, "name length");
            if (result.length() != 0) {
                result.append('.');
            }
            result.append(toAscii ? ascii : unicode);
        }
        // 恢复规范化后的末尾根点，不把它当成空的内部标签。
        return rooted ? result.append('.').toString() : result.toString();
    }

    // ==================== Unicode 属性查询 ====================
    // 打包布局：0..7 位为组合类，8 位为 Mark，9..13 位为 Bidi，14..16 位为连接类型，17..19 位为脚本。
    private int combiningClass(int cp) {
        return property(cp) & 255;
    }

    private int bidi(int cp) {
        return (property(cp) >>> 9) & 31;
    }

    private int joining(int cp) {
        return (property(cp) >>> 14) & 7;
    }

    private int script(int cp) {
        return (property(cp) >>> 17) & 7;
    }

    private int property(int cp) {
        return properties[floor(propertyStarts, cp)];
    }

    private static int floor(int[] starts, int cp) {
        // binarySearch 未命中时返回负的插入位置，向前一项就是码点所属区间。
        int index = Arrays.binarySearch(starts, cp);
        return index < 0 ? -index - 2 : index;
    }

    // ==================== NFC 规范化 ====================
    // 与 IDNA 映射、有效性校验使用同一版本的 Unicode 数据。
    static String normalizeNfc(String text) {
        // ---------- 1. 递归规范分解 ----------
        IdnCodec codec = INSTANCE.get();
        List<Integer> decomposed = new ArrayList<>(text.length());
        text.codePoints().forEach(cp -> codec.decompose(cp, decomposed));

        // ---------- 2. 按组合类重排 ----------
        // 组合类为 0 的字符划分边界，只在各自的非零组合类序列内排序。
        int start = 0, previous = 0;
        boolean ordered = true;
        for (int i = 0; i <= decomposed.size(); i++) {
            int order = i == decomposed.size() ? 0 : codec.combiningClass(decomposed.get(i));
            if (order == 0) {
                if (!ordered) {
                    // 稳定排序保持同类字符的顺序，避免长输入触发插入排序的平方级开销。
                    decomposed.subList(start, i).sort(codec.combiningOrder);
                }
                start = i + 1;
                ordered = true;
            } else if (order < previous) {
                ordered = false;
            }
            previous = order;
        }

        // ---------- 3. 规范合成 ----------
        List<Integer> result = new ArrayList<>(decomposed.size());
        int starter = -1, lastOrder = 0;
        for (int cp : decomposed) {
            int order = codec.combiningClass(cp);
            int composed = -1;
            if (starter >= 0) {
                int first = result.get(starter);
                int leading = first - 0x1100, vowel = cp - 0x1161;
                int syllable = first - 0xac00, trailing = cp - 0x11a7;
                // Hangul 按公式合成，其余码点对查询 Unicode 合成表。
                if (leading >= 0 && leading < 19 && vowel >= 0 && vowel < 21) {
                    composed = 0xac00 + (leading * 21 + vowel) * 28;
                } else if (syllable >= 0 && syllable < 11172 && syllable % 28 == 0 && trailing > 0 && trailing < 28) {
                    composed = first + trailing;
                } else {
                    composed = codec.compositions.getOrDefault(((long) first << 21) | cp, -1);
                }
            }
            // 相同或更高的非零组合类会阻挡当前合成；合成成功时保留 lastOrder。
            if (composed >= 0 && (lastOrder == 0 || lastOrder < order)) {
                result.set(starter, composed);
            } else {
                if (order == 0) {
                    starter = result.size();
                }
                result.add(cp);
                lastOrder = order;
            }
        }

        // ---------- 4. 按码点生成结果 ----------
        StringBuilder normalized = new StringBuilder(text.length());
        for (int cp : result) {
            normalized.appendCodePoint(cp);
        }
        return normalized.toString();
    }

    private void decompose(int cp, List<Integer> result) {
        // ---------- Hangul 音节按公式分解为 L、V 和可选的 T ----------
        int syllable = cp - 0xac00;
        if (syllable >= 0 && syllable < 11172) {
            result.add(0x1100 + syllable / 588);
            result.add(0x1161 + (syllable % 588) / 28);
            if (syllable % 28 != 0) {
                result.add(0x11a7 + syllable % 28);
            }
            return;
        }

        // ---------- 其余字符查表，并递归展开多级规范分解 ----------
        int[] mapping = decompositions.get(cp);
        if (mapping == null) {
            result.add(cp);
        } else {
            for (int item : mapping) {
                decompose(item, result);
            }
        }
    }

    // ==================== RFC 3492 Punycode 编码 ====================
    // 调用方已将输入限制为单个 DNS 标签。
    private static String encodePunycode(int[] input) {
        // ---------- 1. 保留 ASCII 基本码点 ----------
        StringBuilder result = new StringBuilder();
        int handled = 0;
        for (int cp : input) {
            require(cp >= 0 && cp <= 0x10ffff && (cp < 0xd800 || cp > 0xdfff), "Punycode scalar");
            if (cp < 128) {
                result.append((char) cp);
                handled++;
            }
        }
        int basic = handled, bias = 72;
        if (basic > 0) {
            result.append('-');
        }

        // ---------- 2. 逐个编码剩余码点 ----------
        // n 表示当前码点，delta 记录码点距离和插入位置，bias 调整变长整数的阈值。
        long n = 128, delta = 0;
        while (handled < input.length) {
            int minimum = Integer.MAX_VALUE;
            for (int cp : input) {
                if (cp >= n && cp < minimum) {
                    minimum = cp;
                }
            }
            delta += (minimum - n) * (handled + 1L);
            require(delta <= Integer.MAX_VALUE, "Punycode overflow");
            n = minimum;
            for (int cp : input) {
                if (cp < n) {
                    delta++;
                    require(delta <= Integer.MAX_VALUE, "Punycode overflow");
                }
                if (cp == n) {
                    long q = delta;
                    for (int k = 36; ; k += 36) {
                        int threshold = Math.min(26, Math.max(1, k - bias));
                        if (q < threshold) {
                            break;
                        }
                        result.append(PUNYCODE_DIGITS.charAt((int) (threshold + (q - threshold) % (36 - threshold))));
                        q = (q - threshold) / (36 - threshold);
                    }
                    result.append(PUNYCODE_DIGITS.charAt((int) q));
                    bias = adaptPunycodeBias(delta, handled + 1, handled == basic);
                    delta = 0;
                    handled++;
                }
            }
            delta++;
            n++;
        }
        return result.toString();
    }

    // RFC 3492 的 bias 适配算法，编码与解码共用同一组参数。
    private static int adaptPunycodeBias(long delta, int points, boolean first) {
        delta = first ? delta / 700 : delta / 2;
        delta += delta / points;
        int k = 0;
        while (delta > 455) {
            delta /= 35;
            k += 36;
        }
        return k + (int) (36 * delta / (delta + 38));
    }

    // ==================== 通用字符检查与异常 ====================
    private static boolean isAscii(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) >= 128) {
                return false;
            }
        }
        return true;
    }

    // STD3 允许的字母、数字和连字符；大小写已在 ASCII 快速路径或 Unicode 映射中归一化。
    private static boolean isLdh(int cp) {
        return cp == '-' || cp >= 'a' && cp <= 'z' || cp >= '0' && cp <= '9';
    }

    private static void require(boolean condition, String reason) {
        if (!condition) {
            throw new IllegalArgumentException("Invalid IDNA name: " + reason);
        }
    }
}
