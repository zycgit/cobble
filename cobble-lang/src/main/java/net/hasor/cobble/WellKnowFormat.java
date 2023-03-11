/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.cobble;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.function.Function;

/**
 * 时间日期格式
 * @version : 2022-07-25
 * @author 赵永春 (zyc@hasor.net)
 */
public enum WellKnowFormat {
    // java DateTimeFormatter 定义的
    BASIC_ISO_DATE(DateTimeFormatter.BASIC_ISO_DATE),
    ISO_LOCAL_DATE(DateTimeFormatter.ISO_LOCAL_DATE),
    ISO_OFFSET_DATE(DateTimeFormatter.ISO_OFFSET_DATE),
    ISO_DATE(DateTimeFormatter.ISO_DATE),
    ISO_LOCAL_TIME(DateTimeFormatter.ISO_LOCAL_TIME),
    ISO_OFFSET_TIME(DateTimeFormatter.ISO_OFFSET_TIME),
    ISO_TIME(DateTimeFormatter.ISO_TIME),
    ISO_LOCAL_DATE_TIME(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    ISO_OFFSET_DATE_TIME(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
    ISO_ZONED_DATE_TIME(DateTimeFormatter.ISO_ZONED_DATE_TIME),
    ISO_DATE_TIME(DateTimeFormatter.ISO_DATE_TIME),
    ISO_ORDINAL_DATE(DateTimeFormatter.ISO_ORDINAL_DATE),
    ISO_WEEK_DATE(DateTimeFormatter.ISO_WEEK_DATE),
    ISO_INSTANT(DateTimeFormatter.ISO_INSTANT),
    RFC_1123_DATE_TIME(DateTimeFormatter.RFC_1123_DATE_TIME),

    // wel know format
    WKF_DATE(true, DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    WKF_DATE8(true, DateTimeFormatter.ofPattern("yyyyMMdd")),
    //
    WKF_TIME24(true, 0, DateTimeFormatter.ofPattern("HH:mm:ss")),
    WKF_TIME24_S0(true, 0, DateTimeFormatter.ofPattern("HH:mm:ss")),
    WKF_TIME24_S1(true, 1, DateTimeFormatter.ofPattern("HH:mm:ss.S")),
    WKF_TIME24_S2(true, 2, DateTimeFormatter.ofPattern("HH:mm:ss.SS")),
    WKF_TIME24_S3(true, 3, DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),
    WKF_TIME24_S4(true, 4, DateTimeFormatter.ofPattern("HH:mm:ss.SSSS")),
    WKF_TIME24_S5(true, 5, DateTimeFormatter.ofPattern("HH:mm:ss.SSSSS")),
    WKF_TIME24_S6(true, 6, DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS")),
    WKF_TIME24_S7(true, 7, DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSS")),
    WKF_TIME24_S8(true, 8, DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSS")),
    WKF_TIME24_S9(true, 9, DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS")),
    // '2023-02-19 08:35:41', '2023-02-19 08:35:41.123456789'
    WKF_DATE_TIME24(true, 0, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
    WKF_DATE_TIME24_S0(true, 0, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
    WKF_DATE_TIME24_S1(true, 1, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")),
    WKF_DATE_TIME24_S2(true, 2, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS")),
    WKF_DATE_TIME24_S3(true, 3, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
    WKF_DATE_TIME24_S4(true, 4, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS")),
    WKF_DATE_TIME24_S5(true, 5, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSS")),
    WKF_DATE_TIME24_S6(true, 6, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")),
    WKF_DATE_TIME24_S7(true, 7, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSS")),
    WKF_DATE_TIME24_S8(true, 8, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSS")),
    WKF_DATE_TIME24_S9(true, 9, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")),
    // '2023-02-19+04:45'
    WKF_OFFSET_DATE(true, DateTimeFormatter.ofPattern("yyyy-MM-ddxxxxx")),
    WKF_OFFSET_DATE8(true, DateTimeFormatter.ofPattern("yyyyMMddxxxxx")),
    // '08:35:41+04:45', '08:35:41.123456789+04:45'
    WKF_OFFSET_TIME24(true, 0, DateTimeFormatter.ofPattern("HH:mm:ssxxxxx")),
    WKF_OFFSET_TIME24_S0(true, 0, DateTimeFormatter.ofPattern("HH:mm:ssxxxxx")),
    WKF_OFFSET_TIME24_S1(true, 1, DateTimeFormatter.ofPattern("HH:mm:ss.Sxxxxx")),
    WKF_OFFSET_TIME24_S2(true, 2, DateTimeFormatter.ofPattern("HH:mm:ss.SSxxxxx")),
    WKF_OFFSET_TIME24_S3(true, 3, DateTimeFormatter.ofPattern("HH:mm:ss.SSSxxxxx")),
    WKF_OFFSET_TIME24_S4(true, 4, DateTimeFormatter.ofPattern("HH:mm:ss.SSSSxxxxx")),
    WKF_OFFSET_TIME24_S5(true, 5, DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSxxxxx")),
    WKF_OFFSET_TIME24_S6(true, 6, DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSxxxxx")),
    WKF_OFFSET_TIME24_S7(true, 7, DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSxxxxx")),
    WKF_OFFSET_TIME24_S8(true, 8, DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSxxxxx")),
    WKF_OFFSET_TIME24_S9(true, 9, DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSSxxxxx")),
    // '2023-02-19 08:35:41+04:45', '2023-02-19 08:35:41.123456789+04:45'
    WKF_OFFSET_DATE_TIME24(true, 0, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssxxxxx")),
    WKF_OFFSET_DATE_TIME24_S0(true, 0, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssxxxxx")),
    WKF_OFFSET_DATE_TIME24_S1(true, 1, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.Sxxxxx")),
    WKF_OFFSET_DATE_TIME24_S2(true, 2, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSxxxxx")),
    WKF_OFFSET_DATE_TIME24_S3(true, 3, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSxxxxx")),
    WKF_OFFSET_DATE_TIME24_S4(true, 4, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSxxxxx")),
    WKF_OFFSET_DATE_TIME24_S5(true, 5, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSxxxxx")),
    WKF_OFFSET_DATE_TIME24_S6(true, 6, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSxxxxx")),
    WKF_OFFSET_DATE_TIME24_S7(true, 7, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSxxxxx")),
    WKF_OFFSET_DATE_TIME24_S8(true, 8, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSxxxxx")),
    WKF_OFFSET_DATE_TIME24_S9(true, 9, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSSxxxxx")),
    // '2023-02-19 08:35:41+04:45', '2023-02-19 08:35:41.123456789+04:45'
    WKF_OFFSET_DATE_TIME24_ZoneM(true, 0, buildWKFOffsetDateTime24Zone(WKF_DATE_TIME24, "+HH:mm")),
    WKF_OFFSET_DATE_TIME24_ZoneM_S0(true, 0, buildWKFOffsetDateTime24Zone(WKF_DATE_TIME24_S0, "+HH:mm")),
    WKF_OFFSET_DATE_TIME24_ZoneM_S1(true, 1, buildWKFOffsetDateTime24Zone(WKF_DATE_TIME24_S1, "+HH:mm")),
    WKF_OFFSET_DATE_TIME24_ZoneM_S2(true, 2, buildWKFOffsetDateTime24Zone(WKF_DATE_TIME24_S2, "+HH:mm")),
    WKF_OFFSET_DATE_TIME24_ZoneM_S3(true, 3, buildWKFOffsetDateTime24Zone(WKF_DATE_TIME24_S3, "+HH:mm")),
    WKF_OFFSET_DATE_TIME24_ZoneM_S4(true, 4, buildWKFOffsetDateTime24Zone(WKF_DATE_TIME24_S4, "+HH:mm")),
    WKF_OFFSET_DATE_TIME24_ZoneM_S5(true, 5, buildWKFOffsetDateTime24Zone(WKF_DATE_TIME24_S5, "+HH:mm")),
    WKF_OFFSET_DATE_TIME24_ZoneM_S6(true, 6, buildWKFOffsetDateTime24Zone(WKF_DATE_TIME24_S6, "+HH:mm")),
    WKF_OFFSET_DATE_TIME24_ZoneM_S7(true, 7, buildWKFOffsetDateTime24Zone(WKF_DATE_TIME24_S7, "+HH:mm")),
    WKF_OFFSET_DATE_TIME24_ZoneM_S8(true, 8, buildWKFOffsetDateTime24Zone(WKF_DATE_TIME24_S8, "+HH:mm")),
    WKF_OFFSET_DATE_TIME24_ZoneM_S9(true, 9, buildWKFOffsetDateTime24Zone(WKF_DATE_TIME24_S9, "+HH:mm")),
    // '2023-02-19 08:35:41+04:45', '2023-02-19 08:35:41.123456789+04:45'
    WKF_OFFSET_TIME24_ZONE_M(true, 0, buildWKFOffsetDateTime24Zone(WKF_OFFSET_TIME24, "+HH:mm")),
    WKF_OFFSET_TIME24_ZONE_M_S0(true, 0, buildWKFOffsetDateTime24Zone(WKF_OFFSET_TIME24_S0, "+HH:mm")),
    WKF_OFFSET_TIME24_ZONE_M_S1(true, 1, buildWKFOffsetDateTime24Zone(WKF_OFFSET_TIME24_S1, "+HH:mm")),
    WKF_OFFSET_TIME24_ZONE_M_S2(true, 2, buildWKFOffsetDateTime24Zone(WKF_OFFSET_TIME24_S2, "+HH:mm")),
    WKF_OFFSET_TIME24_ZONE_M_S3(true, 3, buildWKFOffsetDateTime24Zone(WKF_OFFSET_TIME24_S3, "+HH:mm")),
    WKF_OFFSET_TIME24_ZONE_M_S4(true, 4, buildWKFOffsetDateTime24Zone(WKF_OFFSET_TIME24_S4, "+HH:mm")),
    WKF_OFFSET_TIME24_ZONE_M_S5(true, 5, buildWKFOffsetDateTime24Zone(WKF_OFFSET_TIME24_S5, "+HH:mm")),
    WKF_OFFSET_TIME24_ZONE_M_S6(true, 6, buildWKFOffsetDateTime24Zone(WKF_OFFSET_TIME24_S6, "+HH:mm")),
    WKF_OFFSET_TIME24_ZONE_M_S7(true, 7, buildWKFOffsetDateTime24Zone(WKF_OFFSET_TIME24_S7, "+HH:mm")),
    WKF_OFFSET_TIME24_ZONE_M_S8(true, 8, buildWKFOffsetDateTime24Zone(WKF_OFFSET_TIME24_S8, "+HH:mm")),
    WKF_OFFSET_TIME24_ZONE_M_S9(true, 9, buildWKFOffsetDateTime24Zone(WKF_OFFSET_TIME24_S9, "+HH:mm")),
    ;

    private final boolean           isWKF;
    private final int               fraction;
    private final DateTimeFormatter ofPattern;

    WellKnowFormat(DateTimeFormatter ofPattern) {
        this(false, 0, ofPattern);
    }

    WellKnowFormat(boolean isWKF, DateTimeFormatter ofPattern) {
        this(isWKF, 0, ofPattern);
    }

    WellKnowFormat(boolean isWKF, int fraction, DateTimeFormatter ofPattern) {
        this.isWKF = isWKF;
        this.fraction = fraction;
        this.ofPattern = ofPattern;
    }

    WellKnowFormat(boolean isWKF, int fraction, Function<DateTimeFormatterBuilder, DateTimeFormatterBuilder> ofPattern) {
        this.isWKF = isWKF;
        this.fraction = fraction;
        this.ofPattern = ofPattern.apply(new DateTimeFormatterBuilder()).toFormatter();
    }

    public DateTimeFormatter toPattern() {
        return this.ofPattern;
    }

    public String format(TemporalAccessor temporal) {
        return this.ofPattern.format(temporal);
    }

    public TemporalAccessor parse(CharSequence text) {
        return this.ofPattern.parse(text);
    }

    public static WellKnowFormat valueOfCode(String code) {
        for (WellKnowFormat wkf : WellKnowFormat.values()) {
            if (StringUtils.equalsIgnoreCase(code, wkf.name())) {
                return wkf;
            }
        }
        return null;
    }

    public static WellKnowFormat valueOfCode(String code, int fraction) {
        WellKnowFormat wkf = valueOfCode(code);
        if (wkf != null && wkf.isWKF && wkf.fraction == 0 && fraction > 0) {
            return valueOfCode(wkf.name() + "_S" + fraction);
        } else {
            return wkf;
        }
    }

    private static DateTimeFormatter buildWKFOffsetDateTime24Zone(WellKnowFormat baseFormatter, String offsetFmt) {
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        builder.append(baseFormatter.ofPattern).appendOffset(offsetFmt, "+00:00");
        return builder.toFormatter();
    }
}