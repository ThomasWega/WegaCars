package me.wega.cars;

import me.wega.cars.toolkit.config.ConfigPropertyClass;
import me.wega.cars.toolkit.config.property.ConfigProperty;
import me.wega.cars.toolkit.file.FileLoader;

import java.io.File;

public class VehiclesMessages extends ConfigPropertyClass {

    public VehiclesMessages() {
        super(WegaCars.INSTANCE, FileLoader.loadSingleFile(WegaCars.INSTANCE, new File(WegaCars.INSTANCE.getDataFolder(), "messages.yml")));
    }

    public static final ConfigProperty<String> CANNOT_DRIVE_DISASSEMBLED = new ConfigProperty<>("cannot-drive-disassembled");
    public static final ConfigProperty<String> INVALID_VEHICLE_ITEM = new ConfigProperty<>("invalid-vehicle-item");
    public static final ConfigProperty<String> INVALID_VEHICLE_TYPE = new ConfigProperty<>("invalid-vehicle-type");
    public static final ConfigProperty<String> NO_NEARBY_LIFTS = new ConfigProperty<>("no-nearby-lifts");
    public static final ConfigProperty<String> ALREADY_SERVICING = new ConfigProperty<>("already-servicing");
    public static final ConfigProperty<String> SUSPENSION_DESTROYED = new ConfigProperty<>("suspension-destroyed");
    public static final ConfigProperty<String> MINIGAME_SUCCESSFUL = new ConfigProperty<>("minigame-successful");
    public static final ConfigProperty<String> NEEDS_RATCHET = new ConfigProperty<>("needs-ratchet");
    public static final ConfigProperty<String> NEEDS_SOCKET = new ConfigProperty<>("needs-socket");
    public static final ConfigProperty<String> MINIGAME_FAILED = new ConfigProperty<>("minigame-failed");
    public static final ConfigProperty<String> ENGINE_NOT_SET_YET = new ConfigProperty<>("engine-not-set-yet");
    public static final ConfigProperty<String> COMBINATION_SET = new ConfigProperty<>("combination-set");
    public static final ConfigProperty<String> COMBINATION_FAILURE = new ConfigProperty<>("combination-failure");
    public static final ConfigProperty<String> PART_BROKEN = new ConfigProperty<>("part-broken");
    public static final ConfigProperty<String> NEEDS_IMPACT_SOCKET = new ConfigProperty<>("needs-impact-socket");
    public static final ConfigProperty<String> NEEDS_WRENCH = new ConfigProperty<>("needs-wrench");
    public static final ConfigProperty<String> REMOVE_JACK_STANDS_FIRST = new ConfigProperty<>("remove-jack-stands-first");
    public static final ConfigProperty<String> SET_JACK_LIFT_FIRST = new ConfigProperty<>("set-jack-lift-first");
    public static final ConfigProperty<String> CANNOT_USE_IN_JACKLIFT = new ConfigProperty<>("cannot-use-in-jacklift");
    public static final ConfigProperty<String> PLEASE_STOP_VEHICLE = new ConfigProperty<>("please-stop-vehicle");
    public static final ConfigProperty<String> REFUELED = new ConfigProperty<>("refueled");
}