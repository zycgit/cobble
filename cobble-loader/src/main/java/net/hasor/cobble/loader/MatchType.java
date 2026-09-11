/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.loader;
/**
 * 扫描器资源匹配类型
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-09-29
 */
public enum MatchType {
    /** 扫描所有资源 */
    None,
    /** 前缀匹配 */
    Prefix,
    /** 含有字符串 */
    ContainsAny,
    /** 尾缀匹配 */
    Suffix,
    /** 全字匹配 */
    Match,
    /** 正则表达式匹配 */
    Regex,
}
