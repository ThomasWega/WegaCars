package me.wega.cars.vehicle.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum VehicleTireSide {
    FRONT_LEFT(true, true),
    FRONT_RIGHT(false, true),
    REAR_LEFT(true, false),
    REAR_RIGHT(false, false);

    private final boolean left;
    private final boolean front;

    public boolean isRight() {
        return !left;
    }

    public boolean isRear() {
        return !front;
    }
}
