package me.wega.cars.item;

import me.wega.cars.VehiclesConfig;
import me.wega.cars.item.ratchet.VehicleRatchet;
import me.wega.cars.toolkit.builder.ItemBuilder;
import me.wega.cars.toolkit.pdc.data.EnumDataType;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public enum VehicleItemType {
    SPRING_COMPRESSOR(VehiclesConfig.SPRING_COMPRESSOR.get().builder()),
    JACK_LIFT(VehiclesConfig.JACK_LIFT.get().builder()),
    JACK_STAND(VehiclesConfig.JACK_STAND.get().builder()),
    RATCHET(VehicleRatchet.createRatchet(VehicleRatchet.RatchetMode.NORMAL)),
    SOCKET(VehiclesConfig.SOCKET.get().builder()),
    SPARKPLUG_SOCKET(VehiclesConfig.SPARKPLUG_SOCKET.get().builder()),
    IMPACT_SOCKET(VehiclesConfig.IMPACT_SOCKET.get().builder()),
    AIR_GUN(VehiclesConfig.AIR_GUN.get().builder()),
    WRENCH(VehiclesConfig.WRENCH.get().builder());

    private final @NotNull ItemStack itemStack;

    VehicleItemType(@NotNull ItemBuilder builder) {
        this.itemStack = builder
                .pdcKey(VehicleItemKey.ITEM_TYPE, this)
                .build();
    }

    public static @Nullable VehicleItemType getType(PersistentDataHolder holder) {
        if (!VehicleItemKey.ITEM_TYPE.has(holder)) return null;

        return (VehicleItemType) VehicleItemKey.ITEM_TYPE.get(holder);
    }

    public static final EnumDataType<VehicleItemType> VEHICLE_ITEM_TYPE_DATA_TYPE = new EnumDataType<>(VehicleItemType.class);
}
