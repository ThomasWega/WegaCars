package me.wega.cars.item.ratchet;

import me.wega.cars.VehiclesConfig;
import me.wega.cars.item.VehicleItemKey;
import me.wega.cars.item.VehicleItemType;
import me.wega.cars.toolkit.builder.ConfigItemBuilder;
import me.wega.cars.toolkit.builder.ItemBuilder;
import me.wega.cars.toolkit.pdc.data.EnumDataType;
import me.wega.cars.toolkit.utils.ColorUtils;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public final class VehicleRatchet {
    private final @NotNull ItemStack itemStack;
    private @NotNull RatchetMode ratchetMode;

    private VehicleRatchet(@NotNull ItemStack itemStack,
                           @NotNull RatchetMode ratchetMode) {
        this.itemStack = itemStack;
        this.ratchetMode = ratchetMode;
    }

    public static boolean isRatchet(@NotNull ItemStack item) {
        final VehicleItemType type = VehicleItemType.getType(item.getItemMeta());
        if (type == null) return false;

        return type == VehicleItemType.RATCHET;
    }

    public static @Nullable VehicleRatchet fromItem(@NotNull ItemStack item) {
        if (!isRatchet(item)) return null;

        final RatchetMode ratchetMode = (RatchetMode) VehicleItemKey.RATCHET_MODE.get(item.getItemMeta());

        return new VehicleRatchet(item, ratchetMode);
    }

    public static @NotNull ItemBuilder createRatchet(@NotNull RatchetMode mode) {
        return VehiclesConfig.RATCHET.get().clone()
                .addTagResolvers(Placeholder.parsed("mode", mode.name()))
                .builder()
                .pdcKey(VehicleItemKey.RATCHET_MODE, mode);
    }

    public void rotateMode() {
        this.ratchetMode = this.ratchetMode.next();
        final ConfigItemBuilder builder = VehiclesConfig.RATCHET.get().clone()
                .addTagResolvers(Placeholder.parsed("mode", this.ratchetMode.name()));

        final ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (builder.getDisplay() != null)
            itemMeta.displayName(ColorUtils.color(builder.getDisplay(), builder.getTagResolvers()));

        itemMeta.lore(ColorUtils.color(builder.getLore(), builder.getTagResolvers()));

        VehicleItemKey.RATCHET_MODE.set(itemMeta, this.ratchetMode);

        this.itemStack.setItemMeta(itemMeta);
    }

    public enum RatchetMode {
        NORMAL, SPARKPLUG;

        private static final RatchetMode[] cached = values();

        public @NotNull RatchetMode next() {
            return cached[(this.ordinal() + 1) % cached.length];
        }

        public static final EnumDataType<RatchetMode> RATCHET_MODE_DATA_TYPE = new EnumDataType<>(RatchetMode.class);
    }
}