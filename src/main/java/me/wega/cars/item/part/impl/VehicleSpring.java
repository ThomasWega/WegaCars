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
public class VehicleSpring extends VehiclePart {

    private final int weightTolerance;
    private final int unsprungWeight;
    public VehicleSpring(@NotNull String itemId,
                         @NotNull String partModel,
                         int maxDurability,
                         @NotNull ConfigItemBuilder itemBuilder,
                         int weightTolerance,
                         int unsprungWeight) {
        super(VehiclePartType.SPRING, itemId, partModel, maxDurability, itemBuilder);
        this.weightTolerance = weightTolerance;
        this.unsprungWeight = unsprungWeight;
    }

    @Override
    public @NotNull Map<VehicleStat, Integer> getStatistics(@NotNull Vehicle vehicle) {
        final Map<VehicleStat, Integer> vehicleStats = new HashMap<>();

        vehicleStats.put(VehicleStat.WEIGHT_TOLERANCE, this.weightTolerance);
        vehicleStats.put(VehicleStat.UNSPRUNG_WEIGHT, this.unsprungWeight);

        return vehicleStats;
    }
}
