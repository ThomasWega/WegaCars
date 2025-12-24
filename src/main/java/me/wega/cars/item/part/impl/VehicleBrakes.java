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
public class VehicleBrakes extends VehiclePart {

    private final int brakingForce;
    private final int unsprungWeight;
    public VehicleBrakes(@NotNull String itemId,
                         @NotNull String partModel,
                         int maxDurability,
                         @NotNull ConfigItemBuilder itemBuilder,
                         int brakingForce,
                         int unsprungWeight) {
        super(VehiclePartType.BRAKES, itemId, partModel, maxDurability, itemBuilder);
        this.brakingForce = brakingForce;
        this.unsprungWeight = unsprungWeight;
    }

    @Override
    public @NotNull Map<VehicleStat, Integer> getStatistics(@NotNull Vehicle vehicle) {
        final Map<VehicleStat, Integer> vehicleStats = new HashMap<>();

        vehicleStats.put(VehicleStat.BRAKING_FORCE, this.brakingForce);
        vehicleStats.put(VehicleStat.UNSPRUNG_WEIGHT, this.unsprungWeight);

        return vehicleStats;
    }
}
