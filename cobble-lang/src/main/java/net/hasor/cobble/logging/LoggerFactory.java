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

import net.hasor.cobble.logging.jdk14.Jdk14LoggingImpl;
import net.hasor.cobble.logging.log4j2.Logger4J2Impl;
import net.hasor.cobble.logging.nologging.NoLoggingImpl;
import net.hasor.cobble.logging.slf4j.Slf4jImpl;
import net.hasor.cobble.logging.stdout.StdOutImpl;

import java.lang.reflect.Constructor;

/**
 * <p>copy form mybatis org.apache.ibatis.logging.LogFactory</p>
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
public final class LoggerFactory {

    /** Marker to be used by logging implementations that support markers. */
    public static final String                        MARKER = "COBBLE";
    private static      Constructor<? extends Logger> logConstructor;

    static {
        tryImplementation(LoggerFactory::useSlf4jLogger);
        tryImplementation(LoggerFactory::useLog4J2Logger);
        //tryImplementation(LoggerFactory::useLog4JLogger);
        tryImplementation(LoggerFactory::useJdkLogger);
        tryImplementation(LoggerFactory::useNoLogger);
    }

    private LoggerFactory() {
        // disable construction
    }

    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    public static Logger getLogger(String logger) {
        try {
            return logConstructor.newInstance(logger);
        } catch (Throwable t) {
            throw new LogException("Error creating logger for logger " + logger + ".  Cause: " + t, t);
        }
    }

    public static synchronized void useCustomLogger(Class<? extends Logger> clazz) {
        setImplementation(clazz);
    }

    public static synchronized void useSlf4jLogger() {
        setImplementation(Slf4jImpl.class);
    }

    //    public static synchronized void useLog4JLogger() {
    //        setImplementation(Logger4JImpl.class);
    //    }

    public static synchronized void useLog4J2Logger() {
        setImplementation(Logger4J2Impl.class);
    }

    public static synchronized void useJdkLogger() {
        setImplementation(Jdk14LoggingImpl.class);
    }

    public static synchronized void useStdOutLogger() {
        setImplementation(StdOutImpl.class);
    }

    public static synchronized void useNoLogger() {
        setImplementation(NoLoggingImpl.class);
    }

    private static void tryImplementation(Runnable runnable) {
        if (logConstructor == null) {
            try {
                runnable.run();
            } catch (Throwable t) {
                // ignore
            }
        }
    }

    private static void setImplementation(Class<? extends Logger> implClass) {
        try {
            Constructor<? extends Logger> candidate = implClass.getConstructor(String.class);
            Logger logger = candidate.newInstance(LoggerFactory.class.getName());
            if (logger.isDebugEnabled()) {
                logger.debug("Logging initialized using '" + implClass + "' adapter.");
            }
            logConstructor = candidate;
        } catch (Throwable t) {
            throw new LogException("Error setting Log implementation.  Cause: " + t, t);
        }
    }

}
