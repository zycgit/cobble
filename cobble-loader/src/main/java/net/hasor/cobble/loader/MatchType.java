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
package net.hasor.cobble.loader;
/**
 * 扫描器资源匹配类型
 * @version : 2021-09-12
 * @author 赵永春 (zyc@hasor.net)
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
