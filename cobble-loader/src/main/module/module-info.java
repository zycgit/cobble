module net.hasor.cobble.loader {
    requires transitive net.hasor.cobble;

    exports net.hasor.cobble.loader;
    exports net.hasor.cobble.loader.jar;
    exports net.hasor.cobble.loader.providers;

    provides java.net.spi.URLStreamHandlerProvider with net.hasor.cobble.loader.jar.HandlerProvider;
}