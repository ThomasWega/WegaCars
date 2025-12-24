package me.wega.cars.item.part;

import me.wega.cars.vehicle.VehicleRepairPart;
import me.wega.cars.vehicle.VehicleStat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
@Getter
public enum VehiclePartType {
    ENGINE(true,
            VehicleRepairPart.HOOD,
            VehiclePartVisibility.DISASSEMBLED_ONLY,
            false,
            VehicleStat.ENGINE_LIFE,
            "engines.json"
    ),
    BRAKES(false,
            VehicleRepairPart.NONE,
            VehiclePartVisibility.DISASSEMBLED_ONLY,
            false,
            VehicleStat.BRAKING_LIFE,
            "brakes.json"
    ),
    SPARKPLUG(true,
            VehicleRepairPart.HOOD,
            VehiclePartVisibility.DISASSEMBLED_ONLY,
            false,
            null,
            "sparkplugs.json"
    ),
    TURBO(true,
            VehicleRepairPart.HOOD,
            VehiclePartVisibility.DISASSEMBLED_ONLY,
            true,
            null,
            "turbos.json"
    ),
    WHEELS(false,
            VehicleRepairPart.NONE,
            VehiclePartVisibility.NEVER,
            false,
            VehicleStat.TIRE_LIFE,
            "wheels.json"
    ),
    FUEL_TANK(true,
            VehicleRepairPart.TRUNK,
            VehiclePartVisibility.DISASSEMBLED_ONLY,
            false,
            null,
            "fuel_tanks.json"
    ),
    SPRING(false,
            VehicleRepairPart.NONE,
            VehiclePartVisibility.NEVER,
            true,
            null,
            "springs.json"
    ),
    SHOCK(false,
            VehicleRepairPart.NONE,
            VehiclePartVisibility.NEVER,
            true,
            null,
            "shocks.json"
    );

    private final boolean active;
    private final @NotNull VehicleRepairPart repairPart;
    private final @NotNull VehiclePartVisibility visibility;
    private final boolean optional;
    private final @Nullable VehicleStat lifeStat;
    private final @NotNull String fileName;

}
