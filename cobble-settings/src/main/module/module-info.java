module net.hasor.cobble.setting {
    requires transitive net.hasor.cobble;
    requires static java.desktop;
    requires static java.sql;

    exports net.hasor.cobble.setting;
    exports net.hasor.cobble.setting.provider;
}