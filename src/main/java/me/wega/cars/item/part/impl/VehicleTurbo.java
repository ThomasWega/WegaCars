package me.wega.cars.item.part.impl;

import me.wega.cars.item.part.VehiclePart;
import me.wega.cars.item.part.VehiclePartType;
import me.wega.cars.vehicle.Vehicle;
import me.wega.cars.vehicle.VehicleStat;
import me.wega.cars.toolkit.builder.ConfigItemBuilder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public class VehicleTurbo extends VehiclePart {

    private final @NotNull TurboType type;
    private final int acceleration;
    private final int topSpeed;
    private final int weight;
    private final int fuelEfficiency;
    public VehicleTurbo(@NotNull String itemId,
                        @NotNull String partModel,
                        int maxDurability,
                        @NotNull ConfigItemBuilder itemBuilder,
                        @NotNull TurboType type,
                        int acceleration,
                        int topSpeed,
                        int weight,
                        int fuelEfficiency) {
        super(VehiclePartType.TURBO, itemId, partModel, maxDurability, itemBuilder);
        this.type = type;
        this.acceleration = acceleration;
        this.topSpeed = topSpeed;
        this.weight = weight;
        this.fuelEfficiency = fuelEfficiency;
    }

    @Override
    public @NotNull Map<VehicleStat, Integer> getStatistics(@NotNull Vehicle vehicle) {
        Map<VehicleStat, Integer> vehicleStats = new HashMap<>();

        vehicleStats.put(VehicleStat.ACCELERATION, this.acceleration);
        vehicleStats.put(VehicleStat.TOP_SPEED, this.topSpeed);
        vehicleStats.put(VehicleStat.COMBINED_WEIGHT, this.weight);
        vehicleStats.put(VehicleStat.FUEL_EFFICIENCY, this.fuelEfficiency);

        return vehicleStats;
    }

    public enum TurboType {
        TURBO,
        SUPER
    }
}
