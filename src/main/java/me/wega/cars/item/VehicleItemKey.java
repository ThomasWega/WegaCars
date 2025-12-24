package me.wega.cars.item;

import me.wega.cars.WegaCars;
import me.wega.cars.item.ratchet.VehicleRatchet;
import me.wega.cars.toolkit.pdc.data.EnumDataType;
import me.wega.cars.toolkit.pdc.key.IPDCKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public enum VehicleItemKey implements IPDCKey {
    // These keys can belong to VehicleItem.
    PART_ID("vehicle-part-id", PersistentDataType.STRING),
    PART_DURABILITY("vehicle-part-durability", PersistentDataType.INTEGER),

    // These keys can belong to custom items.
    ITEM_TYPE("vehicle-item-type", new EnumDataType<>(VehicleItemType.class)),

    // These keys can belong to Suspensions.
    SUSPENSION_SPRING("suspension-spring", PersistentDataType.STRING),
    SUSPENSION_SHOCK("suspension-shock", PersistentDataType.STRING),

    // These keys can belong to a ratchet.
    RATCHET_MODE("ratchet-mode", VehicleRatchet.RatchetMode.RATCHET_MODE_DATA_TYPE);

    private final @NotNull String key;
    private final @NotNull PersistentDataType<?, ?> type;
    private final @NotNull WegaCars instance = WegaCars.INSTANCE;

}
