package me.wega.cars;

import me.wega.cars.toolkit.config.ConfigPropertyClass;
import me.wega.cars.toolkit.config.property.ConfigItemBuilderConfigProperty;
import me.wega.cars.toolkit.config.property.ConfigProperty;
import me.wega.cars.toolkit.file.FileLoader;

import java.io.File;

public class VehiclesConfig extends ConfigPropertyClass {

    public VehiclesConfig() {
        super(WegaCars.INSTANCE, FileLoader.loadSingleFile(WegaCars.INSTANCE, new File(WegaCars.INSTANCE.getDataFolder(), "config.yml")));
    }


    public static final ConfigItemBuilderConfigProperty SUSPENSION_ITEM = new ConfigItemBuilderConfigProperty("items.suspension");

    public static final ConfigItemBuilderConfigProperty SPRING_COMPRESSOR = new ConfigItemBuilderConfigProperty("items.spring-compressor");
    public static final ConfigItemBuilderConfigProperty JACK_LIFT = new ConfigItemBuilderConfigProperty("items.jack-lift");
    public static final ConfigItemBuilderConfigProperty JACK_STAND = new ConfigItemBuilderConfigProperty("items.jack-stand");
    public static final ConfigItemBuilderConfigProperty RATCHET = new ConfigItemBuilderConfigProperty("items.ratchet");
    public static final ConfigItemBuilderConfigProperty SOCKET = new ConfigItemBuilderConfigProperty("items.socket");
    public static final ConfigItemBuilderConfigProperty SPARKPLUG_SOCKET = new ConfigItemBuilderConfigProperty("items.sparkplug-socket");
    public static final ConfigItemBuilderConfigProperty IMPACT_SOCKET = new ConfigItemBuilderConfigProperty("items.impact-socket");
    public static final ConfigItemBuilderConfigProperty AIR_GUN = new ConfigItemBuilderConfigProperty("items.air-gun");
    public static final ConfigItemBuilderConfigProperty WRENCH = new ConfigItemBuilderConfigProperty("items.wrench");


    public static final ConfigProperty<Integer> MAX_LIFT_DISTANCE = new ConfigProperty<>("max-lift-distance");
    public static final ConfigProperty<Integer> STABILITY_MISMATCHED_SHOCKS = new ConfigProperty<>("stability-loss-mismatched-shocks");
}
