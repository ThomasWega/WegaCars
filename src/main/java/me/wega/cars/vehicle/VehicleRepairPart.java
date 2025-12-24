package me.wega.cars.vehicle;

import me.wega.cars.toolkit.pdc.data.EnumDataType;

public enum VehicleRepairPart {
    HOOD,
    WHEEL,
    TRUNK,
    NONE,
    DRIVER;

    public static final EnumDataType<VehicleRepairPart> VEHICLE_REPAIR_PART_DATA_TYPE = new EnumDataType<>(VehicleRepairPart.class);
}
