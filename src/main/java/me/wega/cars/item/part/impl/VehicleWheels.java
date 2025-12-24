package me.wega.cars.item.part.impl;

import com.google.common.collect.Table;
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
public class VehicleWheels extends VehiclePart {

    private final int handling;
    private final @NotNull Table<String, VehicleStat, Integer> environmentTable;
    private final String jackStandModel;
    public VehicleWheels(@NotNull String itemId,
                         @NotNull String partModel,
                         int maxDurability,
                         @NotNull ConfigItemBuilder itemBuilder,
                         int handling,
                         @NotNull Table<String, VehicleStat, Integer> environmentTable,
                         @NotNull String jackStandModel) {
        super(VehiclePartType.WHEELS, itemId, partModel, maxDurability, itemBuilder);
        this.handling = handling;
        this.environmentTable = environmentTable;
        this.jackStandModel = jackStandModel;
    }

    @Override
    public @NotNull Map<VehicleStat, Integer> getStatistics(@NotNull Vehicle vehicle) {
        Map<VehicleStat, Integer> vehicleStats = new HashMap<>();

        vehicleStats.put(VehicleStat.HANDLING, this.handling);

        return vehicleStats;
    }
}
