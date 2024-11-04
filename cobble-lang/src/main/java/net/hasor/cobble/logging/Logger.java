/*
 *    Copyright 2009-2021 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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
