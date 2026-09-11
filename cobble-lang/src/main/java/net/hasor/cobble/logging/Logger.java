/*
 * Copyright 2009-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.logging;
/**
 * <p>copy form mybatis org.apache.ibatis.logging.Log</p>
 * 新增了 getLogger 方法
 * @author Clinton Begin
 * @author 赵永春 (zyc@hasor.net)
 */
public interface Logger {

    static Logger getLogger(String logger) {
        return LoggerFactory.getLogger(logger);
    }

    static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    boolean isDebugEnabled();

    boolean isTraceEnabled();

    void error(String s, Throwable e);

    void error(String s);

    void debug(String s);

    void info(String s);

    void trace(String s);

    void warn(String s);

    void warn(String s, Throwable e);
}
