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
public class VehicleFuelTank extends VehiclePart {

    private final int tankCapacity;
    public VehicleFuelTank(@NotNull String itemId,
                            @NotNull String partModel,
                            int maxDurability,
                            @NotNull ConfigItemBuilder itemBuilder,
                            int tankCapacity) {
        super(VehiclePartType.FUEL_TANK, itemId, partModel, maxDurability, itemBuilder);
        this.tankCapacity = tankCapacity;
    }

    @Override
    public @NotNull Map<VehicleStat, Integer> getStatistics(@NotNull Vehicle vehicle) {
        final Map<VehicleStat, Integer> vehicleStats = new HashMap<>();

        vehicleStats.put(VehicleStat.FUEL_CAPACITY, this.tankCapacity);

        return vehicleStats;
    }
}
