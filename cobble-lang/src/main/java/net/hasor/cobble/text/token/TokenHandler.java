/*
 * Copyright 2009-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.text.token;
/**
 * 原版在 mybatis 中同名类，本类接口增加了2个参数。
 * @author Clinton Begin
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-04-12
 */
public interface TokenHandler {
    String handleToken(StringBuilder hasFound, String openToken, String closeToken, String content);
}
