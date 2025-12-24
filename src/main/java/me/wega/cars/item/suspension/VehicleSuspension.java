package me.wega.cars.item.suspension;

import me.wega.cars.WegaCars;
import me.wega.cars.VehiclesConfig;
import me.wega.cars.item.VehicleItemKey;
import me.wega.cars.item.part.VehiclePart;
import me.wega.cars.item.part.VehiclePartManager;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record VehicleSuspension(@NotNull VehiclePart spring,
                                @NotNull VehiclePart shock) {

    private static final VehiclePartManager ITEM_MANAGER = WegaCars.INSTANCE.getVehiclePartManager();

    public @NotNull ItemStack createItem() {
        return VehiclesConfig.SUSPENSION_ITEM.get().clone()
                .setTagResolvers(
                        Placeholder.parsed("spring", spring.getItemId()),
                        Placeholder.parsed("shock", shock.getItemId())
                )
                .builder()
                .pdcKey(VehicleItemKey.SUSPENSION_SPRING, spring.getItemId())
                .pdcKey(VehicleItemKey.SUSPENSION_SHOCK, shock.getItemId())
                .build();
    }

    public static @Nullable VehicleSuspension fromItem(@NotNull ItemStack itemStack) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (!VehicleItemKey.SUSPENSION_SPRING.has(itemMeta)) return null;

        final VehiclePart spring = ITEM_MANAGER.get(VehicleItemKey.SUSPENSION_SPRING.getString(itemMeta));
        final VehiclePart shock = ITEM_MANAGER.get(VehicleItemKey.SUSPENSION_SHOCK.getString(itemMeta));

        return new VehicleSuspension(spring, shock);
    }
}
