/*
 * Copyright 2008-2009 the original author or authors.
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2013-4-12
 */
public enum DateFormatType {
    // date
    s_yyyyMMdd("\\d{4}-\\d{2}-\\d{2}", "yyyy-MM-dd", true, false, false, 0),
    d_yyyyMMdd("\\d{4}/\\d{2}/\\d{2}", "yyyy/MM/dd", true, false, false, 0),
    // time
    HHmmss("\\d{2}:\\d{2}:\\d{2}", "HH:mm:ss", false, true, false, 0),
    HHmmss_S("\\d{2}:\\d{2}:\\d{2}\\.\\d{1}", "HH:mm:ss.S", false, true, false, 1),
    HHmmss_SS("\\d{2}:\\d{2}:\\d{2}\\.\\d{2}", "HH:mm:ss.SS", false, true, false, 2),
    HHmmss_SSS("\\d{2}:\\d{2}:\\d{2}\\.\\d{3}", "HH:mm:ss.SSS", false, true, false, 3),
    HHmmss_SSSS("\\d{2}:\\d{2}:\\d{2}\\.\\d{4}", "HH:mm:ss.SSSS", false, true, false, 4),
    HHmmss_SSSSS("\\d{2}:\\d{2}:\\d{2}\\.\\d{5}", "HH:mm:ss.SSSSS", false, true, false, 5),
    HHmmss_SSSSSS("\\d{2}:\\d{2}:\\d{2}\\.\\d{6}", "HH:mm:ss.SSSSSS", false, true, false, 6),
    // date use strike like "2021-01-01 12:23:33"
    s_yyyyMMdd_HHmmss("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", "yyyy-MM-dd HH:mm:ss", true, true, false, 0),
    s_yyyyMMdd_HHmmss_S("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{1}", "yyyy-MM-dd HH:mm:ss.S", true, true, false, 1),
    s_yyyyMMdd_HHmmss_SS("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{2}", "yyyy-MM-dd HH:mm:ss.SS", true, true, false, 2),
    s_yyyyMMdd_HHmmss_SSS("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}", "yyyy-MM-dd HH:mm:ss.SSS", true, true, false, 3),
    s_yyyyMMdd_HHmmss_SSSS("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{4}", "yyyy-MM-dd HH:mm:ss.SSSS", true, true, false, 4),
    s_yyyyMMdd_HHmmss_SSSSS("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{5}", "yyyy-MM-dd HH:mm:ss.SSSSS", true, true, false, 5),
    s_yyyyMMdd_HHmmss_SSSSSS("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{6}", "yyyy-MM-dd HH:mm:ss.SSSSSS", true, true, false, 6),
    // date use strike like "2021/01/01 12:23:33"
    d_yyyyMMdd_HHmmss("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}", "yyyy/MM/dd HH:mm:ss", true, true, false, 0),
    d_yyyyMMdd_HHmmss_S("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{1}", "yyyy/MM/dd HH:mm:ss.S", true, true, false, 1),
    d_yyyyMMdd_HHmmss_SS("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{2}", "yyyy/MM/dd HH:mm:ss.SS", true, true, false, 2),
    d_yyyyMMdd_HHmmss_SSS("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}", "yyyy/MM/dd HH:mm:ss.SSS", true, true, false, 3),
    d_yyyyMMdd_HHmmss_SSSS("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{4}", "yyyy/MM/dd HH:mm:ss.SSSS", true, true, false, 4),
    d_yyyyMMdd_HHmmss_SSSSS("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{5}", "yyyy/MM/dd HH:mm:ss.SSSSS", true, true, false, 5),
    d_yyyyMMdd_HHmmss_SSSSSS("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{6}", "yyyy/MM/dd HH:mm:ss.SSSSSS", true, true, false, 6),
    //
    t_yyyyMMdd_HHmmss("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z", "yyyy-MM-dd'T'HH:mm:ss'Z'", true, true, true, 0),
    ;
    private final Pattern regPattern;
    private final String  datePattern;
    private final boolean containsDate;
    private final boolean containsTime;
    private final boolean containsZone;
    private final int     precision;
    private final boolean useJdkDataSafe;

    public Pattern getRegPattern() {
        return this.regPattern;
    }

    public String getDatePattern() {
        return this.datePattern;
    }

    public boolean isContainsDate() {
        return this.containsDate;
    }

    public boolean isContainsTime() {
        return this.containsTime;
    }

    public boolean isContainsZone() {
        return this.containsZone;
    }

    public int getPrecision() {
        return this.precision;
    }

    public boolean isUseJdkDataSafe() {
        return this.useJdkDataSafe;
    }

    DateFormatType(String regPattern, String datePattern, boolean containsDate, boolean containsTime, boolean containsZone, int precision) {
        this.regPattern = Pattern.compile(regPattern);
        this.datePattern = datePattern;
        this.containsDate = containsDate;
        this.containsTime = containsTime;
        this.containsZone = containsZone;
        this.precision = precision;
        this.useJdkDataSafe = precision <= 3; // jdk utils data precision max of 3
    }

    public static DateFormatType passerType(String dataString) {
        for (DateFormatType formatType : DateFormatType.values()) {
            if (formatType.getRegPattern().matcher(dataString).matches()) {
                return formatType;
            }
        }
        throw new UnsupportedOperationException("Unsupported dataFormat value is : " + dataString);
    }

    public Date toDate(String dataString) {
        try {
            return new SimpleDateFormat(this.getDatePattern()).parse(dataString);
        } catch (ParseException e) {
            throw ExceptionUtils.toRuntime(e);
        }
    }

    public LocalDateTime toLocalDateTime(String dataString) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(this.getDatePattern());
        return LocalDateTime.parse(dataString, dateTimeFormatter);
    }

    public static DateFormatType valueOfCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        for (DateFormatType formatType : DateFormatType.values()) {
            if (StringUtils.equalsIgnoreCase(formatType.name(), code)) {
                return formatType;
            }
        }
        return null;
    }
}
