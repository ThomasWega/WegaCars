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
public class VehicleSparkplug extends VehiclePart {

    private final int acceleration;
    private final int expectedLife;
    private final int fuelEfficiency;
    public VehicleSparkplug(@NotNull String itemId,
                            @NotNull String partModel,
                            int maxDurability,
                            @NotNull ConfigItemBuilder itemBuilder,
                            int acceleration,
                            int expectedLife,
                            int fuelEfficiency) {
        super(VehiclePartType.SPARKPLUG, itemId, partModel, maxDurability, itemBuilder);
        this.acceleration = acceleration;
        this.expectedLife = expectedLife;
        this.fuelEfficiency = fuelEfficiency;
    }

    @Override
    public @NotNull Map<VehicleStat, Integer> getStatistics(@NotNull Vehicle vehicle) {
        final Map<VehicleStat, Integer> vehicleStats = new HashMap<>();

        vehicleStats.put(VehicleStat.ACCELERATION, this.acceleration);
        vehicleStats.put(VehicleStat.ENGINE_LIFE, this.expectedLife);
        vehicleStats.put(VehicleStat.FUEL_EFFICIENCY, this.fuelEfficiency);

        return vehicleStats;
    }
}
