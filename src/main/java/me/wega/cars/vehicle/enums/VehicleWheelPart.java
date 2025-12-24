package me.wega.cars.vehicle.enums;

import me.wega.cars.item.part.VehiclePartType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VehicleWheelPart {
    TIRE(VehiclePartType.WHEELS),
    BRAKE(VehiclePartType.BRAKES);

    private final VehiclePartType vehiclePartType;
}
