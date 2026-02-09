package me.wega.cars;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.wega.cars.item.part.VehiclePartType;
import me.wega.cars.toolkit.builder.ConfigItemBuilder;
import me.wega.cars.vehicle.VehicleStat;
import me.wega.cars.toolkit.config.ConfigPropertyClass;
import me.wega.cars.toolkit.config.property.ConfigItemBuilderConfigProperty;
import me.wega.cars.toolkit.config.property.ConfigProperty;
import me.wega.cars.toolkit.file.FileLoader;
import org.bukkit.Material;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehiclesConfig extends ConfigPropertyClass {

    public VehiclesConfig() {
        super(WegaCars.INSTANCE, FileLoader.loadSingleFile(WegaCars.INSTANCE, new File(WegaCars.INSTANCE.getDataFolder(), "config.yml")));
    }

    public static final ConfigProperty<Map<VehicleStat, Float>> VEHICLE_STAT_MULTIPLIERS = new ConfigProperty<>("stat-multipliers", config -> {
        final Map<VehicleStat, Float> vehicleStatFloatMap = new HashMap<>();
        for (VehicleStat vehicleStat : VehicleStat.values()) {
            final float value = (float) config.getDouble("stat-multipliers." + vehicleStat.name());

            vehicleStatFloatMap.put(vehicleStat, value);
        }

        return vehicleStatFloatMap;
    });

    public static final ConfigProperty<Map<VehicleStat, Float>> TUNING_STAT_MODIFIERS = new ConfigProperty<>("tuning-stat-modifiers", config -> {
        final Map<VehicleStat, Float> vehicleStatFloatMap = new HashMap<>();
        for (VehicleStat vehicleStat : VehicleStat.values()) {
            final float value = (float) config.getDouble("tuning-stat-modifiers." + vehicleStat.name(), 0.0);
            vehicleStatFloatMap.put(vehicleStat, value);
        }

        return vehicleStatFloatMap;
    });

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
    public static final ConfigItemBuilderConfigProperty FUEL_CAN = new ConfigItemBuilderConfigProperty("items.fuel-can");

    public static final ConfigItemBuilderConfigProperty BOLT_ITEM = new ConfigItemBuilderConfigProperty("items.bolt");

    public static final ConfigProperty<Integer> MAX_DURABILITY_ROLL = new ConfigProperty<>("max-durability-roll");
    public static final ConfigProperty<Integer> MISMATCHED_TIRES_PENALTY = new ConfigProperty<>("mismatched-tires-penalty");
    public static final ConfigProperty<Integer> FUEL_TICK_TIMER = new ConfigProperty<>("fuel-tick-timer");
    public static final ConfigProperty<Integer> DURABILITY_TICK_TIMER = new ConfigProperty<>("durability-tick-timer");
    public static final ConfigProperty<Integer> MANUAL_TICK_TIMER = new ConfigProperty<>("manual-tick-timer");

    public static final ConfigProperty<Double> VELOCITY_VECTOR_MULTIPLIER = new ConfigProperty<>("velocity-vector-multiplier");
    public static final ConfigProperty<Double> SMOOTHING_FACTOR = new ConfigProperty<>("smoothing-factor");
    public static final ConfigProperty<Integer> MAX_LIFT_DISTANCE = new ConfigProperty<>("max-lift-distance");
    public static final ConfigProperty<Integer> ENGINE_SET_TIME = new ConfigProperty<>("engine-set-time");
    public static final ConfigProperty<Integer> STABILITY_MISMATCHED_SHOCKS = new ConfigProperty<>("stability-loss-mismatched-shocks");

    public static final ConfigProperty<String> FUEL_BAR_FORMAT = new ConfigProperty<>("fuel-bar.format");
    public static final ConfigProperty<Integer> FUEL_BAR_LENGTH = new ConfigProperty<>("fuel-bar.length");
    public static final ConfigProperty<String> FUEL_BAR_CHARACTER = new ConfigProperty<>("fuel-bar.symbol");
    public static final ConfigProperty<String> FUEL_BAR_COLOR = new ConfigProperty<>("fuel-bar.color");
    public static final ConfigProperty<String> FUEL_BAR_COLOR_AVAILABLE = new ConfigProperty<>("fuel-bar.available-color");

    public static final ConfigProperty<String> MANUAL_BAR_SUBTITLE = new ConfigProperty<>("manual-bar.subtitle");
    public static final ConfigProperty<Integer> MANUAL_BAR_LENGTH = new ConfigProperty<>("manual-bar.length");
    public static final ConfigProperty<String> MANUAL_BAR_CHARACTER = new ConfigProperty<>("manual-bar.symbol");
    public static final ConfigProperty<String> MANUAL_BAR_CORRECT_COLOR = new ConfigProperty<>("manual-bar.correct-color");
    public static final ConfigProperty<String> MANUAL_BAR_INCORRECT_COLOR = new ConfigProperty<>("manual-bar.incorrect-color");
    public static final ConfigProperty<String> MANUAL_BAR_CURRENT_COLOR = new ConfigProperty<>("manual-bar.current-color");
    public static final ConfigProperty<Integer> MANUAL_TRANSMISSION_SPEED_BOOST_TIME = new ConfigProperty<>("manual-transmission-speed-boost-time");

    public static final ConfigProperty<Table<Material, Boolean, String>> ENVIRONMENT_NAME_TABLE = new ConfigProperty<>("environments", config -> {
        final Table<Material, Boolean, String> table = HashBasedTable.create();
        for (String key : config.getConfigurationSection("environments").getKeys(false)) {
            boolean rain = config.getBoolean("environments." + key + ".rain");
            List<String> materials = config.getStringList("environments." + key + ".materials");
            for (String material : materials) {
                table.put(Material.getMaterial(material), rain, key);
            }
        }

        return table;
    });

    public static class GUI {
        public static final ConfigProperty<Map<VehiclePartType, ConfigItemBuilder>> NO_PART_ITEMS = new ConfigProperty<>("gui-items.empty", config -> {
            final Map<VehiclePartType, ConfigItemBuilder> map = new HashMap<>();
            for (VehiclePartType vehiclePartType : VehiclePartType.values()) {
                final ConfigItemBuilder builder = new ConfigItemBuilder(config.getConfigurationSection("gui-items.empty." + vehiclePartType.name()));
                map.put(vehiclePartType, builder);
            }

            return map;
        });
        public static final ConfigItemBuilderConfigProperty CREATE_SUSPENSION_ITEM = new ConfigItemBuilderConfigProperty("gui-items.create-suspension");
        public static final ConfigItemBuilderConfigProperty NO_SUSPENSION_ITEM = new ConfigItemBuilderConfigProperty("gui-items.empty.SUSPENSION");
        public static final ConfigItemBuilderConfigProperty ALIGNMENT_COMBINATION = new ConfigItemBuilderConfigProperty("gui-items.alignment-combination");
        public static final ConfigItemBuilderConfigProperty TIRE_ALIGNMENT_SELECTION = new ConfigItemBuilderConfigProperty("gui-items.alignment-combination-option");
        public static final ConfigItemBuilderConfigProperty TUNE_UP_ITEM = new ConfigItemBuilderConfigProperty("gui-items.tune-up");
    }

    public static class Minigame {
        public static final ConfigItemBuilderConfigProperty MINIGAME_INFO_ITEM = new ConfigItemBuilderConfigProperty("minigame.set-engine.info-item");
        public static final ConfigItemBuilderConfigProperty ROTATE_COLUMN_1_ITEM = new ConfigItemBuilderConfigProperty("minigame.set-engine.rotate-column-1-item");
        public static final ConfigItemBuilderConfigProperty ROTATE_ROW_1_ITEM = new ConfigItemBuilderConfigProperty("minigame.set-engine.rotate-row-1-item");
        public static final ConfigItemBuilderConfigProperty ROTATE_COLUMN_2_ITEM = new ConfigItemBuilderConfigProperty("minigame.set-engine.rotate-column-2-item");
        public static final ConfigItemBuilderConfigProperty ROTATE_COLUMN_3_ITEM = new ConfigItemBuilderConfigProperty("minigame.set-engine.rotate-column-3-item");
        public static final ConfigItemBuilderConfigProperty CONNECT_ITEM = new ConfigItemBuilderConfigProperty("minigame.set-engine.connect-item");

        public static final ConfigItemBuilderConfigProperty UP_ITEM = new ConfigItemBuilderConfigProperty("minigame.tuneup.up-item");
        public static final ConfigItemBuilderConfigProperty DOWN_ITEM = new ConfigItemBuilderConfigProperty("minigame.tuneup.down-item");
        public static final ConfigItemBuilderConfigProperty LEFT_ITEM = new ConfigItemBuilderConfigProperty("minigame.tuneup.left-item");
        public static final ConfigItemBuilderConfigProperty RIGHT_ITEM = new ConfigItemBuilderConfigProperty("minigame.tuneup.right-item");
        public static final ConfigProperty<Integer> MAX_TURNS = new ConfigProperty<>("minigame.tuneup.max-turns");
        public static final ConfigProperty<Double> MAX_REPAIR_FACTOR = new ConfigProperty<>("minigame.tuneup.max-repair-factor");
        public static final ConfigProperty<Integer> MINIMUM_DRIVE_TIME = new ConfigProperty<>("minigame.tuneup.min-drive-time");
    }

    public static class Titles {
        public static final ConfigProperty<String> ENGINE_REMOVAL_MINIGAME_TITLE = new ConfigProperty<>("inventory-titles.engine-removal-minigame");
        public static final ConfigProperty<String> ENGINE_SET_MINIGAME_TITLE = new ConfigProperty<>("inventory-titles.engine-set-minigame");
        public static final ConfigProperty<String> SET_ALIGNMENT_MINIGAME_TITLE = new ConfigProperty<>("inventory-titles.set-alignment-minigame");
        public static final ConfigProperty<String> FIRST_SUSPENSION_REPLACEMENT_MINIGAME_TITLE = new ConfigProperty<>("inventory-titles.first-suspension-replacement-minigame");
        public static final ConfigProperty<String> SECOND_SUSPENSION_REPLACEMENT_MINIGAME_TITLE = new ConfigProperty<>("inventory-titles.second-suspension-replacement-minigame");
        public static final ConfigProperty<String> TUNEUP_MINIGAME_TITLE = new ConfigProperty<>("inventory-titles.tuneup-minigame");

        public static final ConfigProperty<String> ALIGNMENT_SELECTION_TITLE = new ConfigProperty<>("inventory-titles.alignment-selection");
        public static final ConfigProperty<String> ENGINE_PARTS_TITLE = new ConfigProperty<>("inventory-titles.engine-parts");
        public static final ConfigProperty<String> SPARKPLUG_PARTS_TITLE = new ConfigProperty<>("inventory-titles.sparkplug-parts");
        public static final ConfigProperty<String> TIRE_PARTS_TITLE = new ConfigProperty<>("inventory-titles.tire-parts");
        public static final ConfigProperty<String> TRUNK_PARTS_TITLE = new ConfigProperty<>("inventory-titles.trunk-parts");
        public static final ConfigProperty<String> SPRING_COMPRESSOR_TITLE = new ConfigProperty<>("inventory-titles.spring-compressor");
    }
}
