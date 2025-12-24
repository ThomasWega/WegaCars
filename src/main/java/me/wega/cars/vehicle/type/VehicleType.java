package me.wega.cars.vehicle.type;

import com.google.common.collect.Table;
import me.wega.cars.item.part.VehiclePartType;
import me.wega.cars.item.part.impl.VehicleShock;
import me.wega.cars.item.part.impl.VehicleTurbo;
import me.wega.cars.vehicle.VehicleStat;
import me.wega.cars.vehicle.enums.VehicleAlignmentCombination;
import me.wega.cars.vehicle.holder.VehicleInteractionData;
import me.wega.cars.vehicle.holder.VehicleTireData;
import me.wega.cars.vehicle.transmission.TransmissionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class VehicleType {

    private final @NotNull String id;
    private final @NotNull String vehicleName;
    private final @NotNull String assembledModel;
    private final @NotNull String disassembledModel;
    private final @NotNull String jackLiftModel;
    private final @NotNull VehicleInteractionData driveOffset;
    private final @NotNull VehicleInteractionData hoodOffset;
    private final @NotNull VehicleInteractionData trunkOffset;
    private final @NotNull TransmissionType transmissionType;
    private final @NotNull Map<VehiclePartType, Vector> offsetMap;
    private final @NotNull List<VehicleTireData> tireOffsetList;
    private final @NotNull Table<VehicleAlignmentCombination, VehicleStat, Integer> alignmentTable;
    private final @NotNull VehicleTurbo.TurboType turboType;
    private final @NotNull VehicleShock.ShockType shockType;

    public @NotNull Vector getOffset(@NotNull VehiclePartType type) {
        return offsetMap.getOrDefault(type, new Vector(0, 0, 0));
    }
}
