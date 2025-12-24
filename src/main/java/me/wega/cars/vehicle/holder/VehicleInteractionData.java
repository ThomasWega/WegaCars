package me.wega.cars.vehicle.holder;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public record VehicleInteractionData(@NotNull Vector vector,
                                     float width,
                                     float height) {
}
