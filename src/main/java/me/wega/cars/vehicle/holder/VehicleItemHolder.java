package me.wega.cars.vehicle.holder;

import me.wega.cars.item.part.VehiclePart;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@AllArgsConstructor
public final class VehicleItemHolder {
    private final @NotNull VehiclePart vehiclePart;
    private int durability;

    public void decreaseDurability() {
        this.durability--;
    }

    public @NotNull ItemStack getItemStack() {
        return this.vehiclePart.getItemStack(this.durability);
    }
}
