package me.wega.cars.vehicle.holder;

import me.wega.cars.vehicle.enums.VehicleTireSide;
import org.jetbrains.annotations.NotNull;

public record VehicleTireData(@NotNull VehicleInteractionData data,
                              @NotNull VehicleTireSide side) {
}
