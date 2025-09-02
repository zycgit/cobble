module net.hasor.cobble.dynamic {
    requires transitive net.hasor.cobble;

    requires static java.desktop;
    requires static java.sql;
    requires static org.objectweb.asm;

    exports net.hasor.cobble.dynamic;
}