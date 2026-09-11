/*
 * Copyright 2009-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.logging.stdout;
import net.hasor.cobble.logging.Logger;

/**
 * <p>copy form mybatis org.apache.ibatis.logging.stdout.StdOutImpl</p>
 * @author Clinton Begin
 */
public class StdOutImpl implements Logger {

    public StdOutImpl(String clazz) {
        // Do Nothing
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void error(String s, Throwable e) {
        System.err.println(s);
        if (e != null) {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public void error(String s) {
        System.err.println(s);
    }

    @Override
    public void debug(String s) {
        System.out.println(s);
    }

    @Override
    public void info(String s) {
        System.out.println(s);
    }

    @Override
    public void trace(String s) {
        System.out.println(s);
    }

    @Override
    public void warn(String s) {
        System.out.println(s);
    }

    @Override
    public void warn(String s, Throwable e) {
        System.out.println(s);
        if (e != null) {
            e.printStackTrace(System.out);
        }
    }
}
