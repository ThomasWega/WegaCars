package me.wega.cars.toolkit.config.impl;

import me.wega.cars.WegaCars;
import me.wega.cars.toolkit.config.ConfigPropertyClass;
import me.wega.cars.toolkit.config.property.ConfigItemBuilderConfigProperty;
import me.wega.cars.toolkit.file.FileLoader;

import java.io.File;

public class GUIItemsConfig extends ConfigPropertyClass {

    public GUIItemsConfig() {
        super(WegaCars.INSTANCE, FileLoader.loadSingleFile(WegaCars.INSTANCE, new File(WegaCars.INSTANCE.getDataFolder(), "gui/items.yml")));
    }

    public static class Navigation {
        public static final ConfigItemBuilderConfigProperty NEXT_PAGE = new ConfigItemBuilderConfigProperty("navigation.next-page");
        public static final ConfigItemBuilderConfigProperty PREVIOUS_PAGE = new ConfigItemBuilderConfigProperty("navigation.previous-page");
        public static final ConfigItemBuilderConfigProperty GO_BACK = new ConfigItemBuilderConfigProperty("navigation.go-back");
        public static final ConfigItemBuilderConfigProperty CLOSE = new ConfigItemBuilderConfigProperty("navigation.close");
    }
}
