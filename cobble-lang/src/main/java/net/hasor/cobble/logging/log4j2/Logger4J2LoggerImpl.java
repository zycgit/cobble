/*
 * Copyright 2009-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.logging.log4j2;
import net.hasor.cobble.logging.Logger;
import net.hasor.cobble.logging.LoggerFactory;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * <p>copy form mybatis org.apache.ibatis.logging.log4j2.Log4j2LoggerImpl</p>
 * @author Eduardo Macarron
 */
public class Logger4J2LoggerImpl implements Logger {
    private static final Marker                          MARKER = MarkerManager.getMarker(LoggerFactory.MARKER);
    private final        org.apache.logging.log4j.Logger logger;

    public Logger4J2LoggerImpl(org.apache.logging.log4j.Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void error(String s, Throwable e) {
        logger.error(MARKER, s, e);
    }

    @Override
    public void error(String s) {
        logger.error(MARKER, s);
    }

    @Override
    public void debug(String s) {
        logger.debug(MARKER, s);
    }

    @Override
    public void info(String s) {
        logger.info(s);
    }

    @Override
    public void trace(String s) {
        logger.trace(MARKER, s);
    }

    @Override
    public void warn(String s) {
        logger.warn(MARKER, s);
    }

    @Override
    public void warn(String s, Throwable e) {
        logger.warn(MARKER, s, e);
    }

}
