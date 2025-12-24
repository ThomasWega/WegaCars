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
public class VehicleEngine extends VehiclePart {

    private final int topSpeed;
    private final int acceleration;
    private final int weight;
    private final int engineLife;
    public VehicleEngine(@NotNull String itemId,
                         @NotNull String partModel,
                         int maxDurability,
                         @NotNull ConfigItemBuilder itemBuilder,
                         int topSpeed,
                         int acceleration,
                         int weight,
                         int engineLife) {
        super(VehiclePartType.ENGINE, itemId, partModel, maxDurability, itemBuilder);
        this.topSpeed = topSpeed;
        this.acceleration = acceleration;
        this.weight = weight;
        this.engineLife = engineLife;
    }

    @Override
    public @NotNull Map<VehicleStat, Integer> getStatistics(@NotNull Vehicle vehicle) {
        final Map<VehicleStat, Integer> vehicleStats = new HashMap<>();

        vehicleStats.put(VehicleStat.TOP_SPEED, this.topSpeed);
        vehicleStats.put(VehicleStat.ACCELERATION, this.acceleration);
        vehicleStats.put(VehicleStat.COMBINED_WEIGHT, this.weight);
        vehicleStats.put(VehicleStat.ENGINE_LIFE, this.engineLife);

        return vehicleStats;
    }
}
