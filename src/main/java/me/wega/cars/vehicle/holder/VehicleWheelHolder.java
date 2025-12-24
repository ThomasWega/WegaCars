package me.wega.cars.vehicle.holder;

import me.wega.cars.vehicle.enums.VehicleAlignmentCombination;
import me.wega.cars.vehicle.enums.VehicleWheelPart;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
public final class VehicleWheelHolder {

    private final @NotNull Map<VehicleWheelPart, VehicleItemHolder> wheelParts;
    private VehicleAlignmentCombination alignmentCombination = VehicleAlignmentCombination.NO_TOE_NO_CAMBER;
    private boolean jackStand;

    public boolean hasTire() {
        return this.wheelParts.containsKey(VehicleWheelPart.TIRE);
    }

    public boolean hasBrakes() {
        return this.wheelParts.containsKey(VehicleWheelPart.BRAKE);
    }
}