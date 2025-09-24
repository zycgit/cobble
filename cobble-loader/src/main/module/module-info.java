module net.hasor.cobble.loader {
    requires transitive net.hasor.cobble;
    requires static java.desktop;
    requires static java.sql;

    exports net.hasor.cobble.loader;
    exports net.hasor.cobble.loader.jar;
    exports net.hasor.cobble.loader.providers;

    provides java.net.spi.URLStreamHandlerProvider with net.hasor.cobble.loader.jar.HandlerProvider;
}