package me.wega.cars.item.part;

import me.wega.cars.WegaCars;
import me.wega.cars.item.VehicleItemKey;
import me.wega.cars.vehicle.Vehicle;
import me.wega.cars.vehicle.VehicleStat;
import me.wega.cars.toolkit.builder.ConfigItemBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class VehiclePart {
    private final @NotNull VehiclePartType itemType;
    private final @NotNull String itemId;
    private final @NotNull String partModel;
    private final int maxDurability;
    private final @NotNull ConfigItemBuilder itemBuilder;

    public static @Nullable VehiclePart fromItem(@NotNull ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) return null;

        final ItemMeta meta = itemStack.getItemMeta();
        final String id = VehicleItemKey.PART_ID.getString(meta);
        if (id == null) return null;

        return WegaCars.INSTANCE.getVehiclePartManager().get(id);
    }

    public static int getDurability(@NotNull ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) return -1;

        final ItemMeta meta = itemStack.getItemMeta();
        final Integer value = VehicleItemKey.PART_DURABILITY.getInt(meta);
        if (value == null) return -1;

        return value;
    }

    public @NotNull ItemStack getNewItemStack() {
        return this.getItemStack(maxDurability);
    }

    public @NotNull ItemStack getItemStack(int durability) {
        final Material material = Material.getMaterial(itemBuilder.getMaterial());
        return itemBuilder
                .setTagResolvers(
                        Placeholder.parsed("durability", durability + ""),
                        Placeholder.parsed("max_durability", maxDurability + "")
                )
                .builder()
                .pdcKey(VehicleItemKey.PART_ID, itemId)
                .pdcKey(VehicleItemKey.PART_DURABILITY, durability)
                .damage((short) (material.getMaxDurability() - (((double) durability / maxDurability) * material.getMaxDurability())))
                .build();
    }

    public abstract @NotNull Map<VehicleStat, Integer> getStatistics(@NotNull Vehicle vehicle);
}
