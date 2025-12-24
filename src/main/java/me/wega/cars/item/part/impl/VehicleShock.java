package me.wega.cars.item.part.impl;

import me.wega.cars.VehiclesConfig;
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
public class VehicleShock extends VehiclePart {

    private final @NotNull ShockType shockType;
    private final int stability;
    private final int unsprungWeight;
    public VehicleShock(@NotNull String itemId,
                         @NotNull String partModel,
                         int maxDurability,
                         @NotNull ConfigItemBuilder itemBuilder,
                         @NotNull ShockType shockType,
                         int stability,
                         int unsprungWeight) {
        super(VehiclePartType.SHOCK, itemId, partModel, maxDurability, itemBuilder);
        this.shockType = shockType;
        this.stability = stability;
        this.unsprungWeight = unsprungWeight;
    }

    @Override
    public @NotNull Map<VehicleStat, Integer> getStatistics(@NotNull Vehicle vehicle) {
        final Map<VehicleStat, Integer> vehicleStats = new HashMap<>();

        vehicleStats.put(VehicleStat.STABILITY, this.stability);
        vehicleStats.put(VehicleStat.UNSPRUNG_WEIGHT, this.unsprungWeight);

        if (vehicle.getSuspension() != null) {
            final VehicleShock part = (VehicleShock) vehicle.getSuspension().shock();
            if (part.getShockType() != this.shockType) {
                vehicleStats.put(VehicleStat.STABILITY, this.stability - VehiclesConfig.STABILITY_MISMATCHED_SHOCKS.get());
            }
        }

        return vehicleStats;
    }

    public enum ShockType {
        SHOCK,
        STRUT
    }
}