package me.wega.cars.vehicle;

import me.wega.cars.WegaCars;
import com.jeff_media.morepersistentdatatypes.DataType;
import me.wega.cars.toolkit.pdc.key.IPDCKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public enum VehicleKey implements IPDCKey {
    ID("vehicle-id", DataType.UUID),
    REPAIR_PART("vehicle-repair-part", VehicleRepairPart.VEHICLE_REPAIR_PART_DATA_TYPE),
    TIRE_INDEX("vehicle-tire-index", PersistentDataType.INTEGER);

    private final @NotNull String key;
    private final @NotNull PersistentDataType<?, ?> type;
    private final @NotNull WegaCars instance = WegaCars.INSTANCE;
}
