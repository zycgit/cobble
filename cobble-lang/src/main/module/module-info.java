module net.hasor.cobble {
    requires static org.objectweb.asm;
    requires static java.sql;
    requires static java.desktop;
    requires static java.logging;
    requires static org.slf4j;
    requires static org.apache.logging.log4j;

    exports net.hasor.cobble;
    exports net.hasor.cobble.bus;
    exports net.hasor.cobble.codec;
    exports net.hasor.cobble.concurrent;
    exports net.hasor.cobble.concurrent.future;
    exports net.hasor.cobble.concurrent.timer;
    exports net.hasor.cobble.convert;
    exports net.hasor.cobble.function;
    exports net.hasor.cobble.i18n;
    exports net.hasor.cobble.io;
    exports net.hasor.cobble.io.bytes;
    exports net.hasor.cobble.io.input;
    exports net.hasor.cobble.io.output;
    exports net.hasor.cobble.io.output.hole;
    exports net.hasor.cobble.io.reader;
    exports net.hasor.cobble.logging;
    exports net.hasor.cobble.provider;
    exports net.hasor.cobble.ref;
    exports net.hasor.cobble.reflect;
    exports net.hasor.cobble.reflect.resolvable;
    exports net.hasor.cobble.text;
    exports net.hasor.cobble.text.token;
    exports net.hasor.cobble.time;
}