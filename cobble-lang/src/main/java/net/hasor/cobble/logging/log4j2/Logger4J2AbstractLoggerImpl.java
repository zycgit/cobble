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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;

/**
 * <p>copy form mybatis org.apache.ibatis.logging.log4j2.Log4j2AbstractLoggerImpl</p>
 * @author Eduardo Macarron
 */
public class Logger4J2AbstractLoggerImpl implements Logger {
    private static final Marker                MARKER = MarkerManager.getMarker(LoggerFactory.MARKER);
    private static final String                FQCN   = Logger4J2Impl.class.getName();
    private final        ExtendedLoggerWrapper log;

    public Logger4J2AbstractLoggerImpl(AbstractLogger abstractLogger) {
        log = new ExtendedLoggerWrapper(abstractLogger, abstractLogger.getName(), abstractLogger.getMessageFactory());
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    @Override
    public void error(String s, Throwable e) {
        log.logIfEnabled(FQCN, Level.ERROR, MARKER, (Message) new SimpleMessage(s), e);
    }

    @Override
    public void error(String s) {
        log.logIfEnabled(FQCN, Level.ERROR, MARKER, (Message) new SimpleMessage(s), null);
    }

    @Override
    public void debug(String s) {
        log.logIfEnabled(FQCN, Level.DEBUG, MARKER, (Message) new SimpleMessage(s), null);
    }

    @Override
    public void info(String s) {
        log.logIfEnabled(FQCN, Level.INFO, MARKER, (Message) new SimpleMessage(s), null);
    }

    @Override
    public void trace(String s) {
        log.logIfEnabled(FQCN, Level.TRACE, MARKER, (Message) new SimpleMessage(s), null);
    }

    @Override
    public void warn(String s) {
        log.logIfEnabled(FQCN, Level.WARN, MARKER, (Message) new SimpleMessage(s), null);
    }

    @Override
    public void warn(String s, Throwable e) {
        log.logIfEnabled(FQCN, Level.WARN, MARKER, (Message) new SimpleMessage(s), e);
    }

}
