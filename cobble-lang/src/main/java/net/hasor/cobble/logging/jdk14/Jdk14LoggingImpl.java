/*
 * Copyright 2009-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.logging.jdk14;
import java.util.logging.Level;
import net.hasor.cobble.logging.Logger;

/**
 * <p>copy form mybatis org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl</p>
 * @author Clinton Begin
 */
public class Jdk14LoggingImpl implements Logger {
    private final java.util.logging.Logger logger;

    public Jdk14LoggingImpl(String clazz) {
        logger = java.util.logging.Logger.getLogger(clazz);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isLoggable(Level.FINER);
    }

    @Override
    public void error(String s, Throwable e) {
        logger.log(Level.SEVERE, s, e);
    }

    @Override
    public void error(String s) {
        logger.log(Level.SEVERE, s);
    }

    @Override
    public void debug(String s) {
        logger.log(Level.FINE, s);
    }

    @Override
    public void info(String s) {
        logger.log(Level.INFO, s);
    }

    @Override
    public void trace(String s) {
        logger.log(Level.FINER, s);
    }

    @Override
    public void warn(String s) {
        logger.log(Level.WARNING, s);
    }

    @Override
    public void warn(String s, Throwable e) {
        logger.log(Level.WARNING, s, e);
    }

}
