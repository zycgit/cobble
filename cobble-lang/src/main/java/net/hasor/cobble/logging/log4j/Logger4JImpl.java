///*
// *    Copyright 2009-2021 the original author or authors.
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *       http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//package net.hasor.cobble.logging.log4j;
//
//import net.hasor.cobble.logging.Logger;
//import org.apache.log4j.Level;
//
///**
// * <p>copy form mybatis org.apache.ibatis.logging.log4j.Log4jImpl</p>
// * @author Eduardo Macarron
// */
//public class Logger4JImpl implements Logger {
//    private static final String                  FQCN = Logger4JImpl.class.getName();
//    private final        org.apache.log4j.Logger logger;
//
//    public Logger4JImpl(String clazz) {
//        logger = org.apache.log4j.Logger.getLogger(clazz);
//    }
//
//    @Override
//    public boolean isDebugEnabled() {
//        return logger.isDebugEnabled();
//    }
//
//    @Override
//    public boolean isTraceEnabled() {
//        return logger.isTraceEnabled();
//    }
//
//    @Override
//    public void error(String s, Throwable e) {
//        logger.log(FQCN, Level.ERROR, s, e);
//    }
//
//    @Override
//    public void error(String s) {
//        logger.log(FQCN, Level.ERROR, s, null);
//    }
//
//    @Override
//    public void debug(String s) {
//        logger.log(FQCN, Level.DEBUG, s, null);
//    }
//
//    @Override
//    public void info(String s) {
//        logger.log(FQCN, Level.INFO, s, null);
//    }
//
//    @Override
//    public void trace(String s) {
//        logger.log(FQCN, Level.TRACE, s, null);
//    }
//
//    @Override
//    public void warn(String s) {
//        logger.log(FQCN, Level.WARN, s, null);
//    }
//
//    @Override
//    public void warn(String s, Throwable e) {
//        logger.log(FQCN, Level.WARN, s, e);
//    }
//
//}
