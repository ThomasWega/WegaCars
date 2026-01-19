package me.wega.cars.vehicle.gui;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import me.wega.cars.WegaCars;
import me.wega.cars.VehiclesConfig;
import me.wega.cars.item.part.VehiclePart;
import me.wega.cars.item.part.VehiclePartType;
import me.wega.cars.vehicle.Vehicle;
import me.wega.cars.vehicle.VehicleRepairPart;
import me.wega.cars.vehicle.holder.VehicleItemHolder;
import me.wega.cars.toolkit.task.Tasks;
import me.wega.cars.toolkit.utils.ColorUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TrunkVehicleGUI extends ChestGui {

    private final @NotNull Vehicle vehicle;

    private final OutlinePane partsPane = new OutlinePane(1, 1, 7, 1);
    public TrunkVehicleGUI(@NotNull Vehicle vehicle) {
        super(3, ComponentHolder.of(ColorUtils.color(VehiclesConfig.Titles.TRUNK_PARTS_TITLE.get())));
        this.vehicle = vehicle;
        this.initialize();
    }

    private void initialize() {
        this.setOnGlobalClick(event -> event.setCancelled(true));

        for (VehiclePartType type : VehiclePartType.values()) {
            if (!type.isActive()) continue;
            if (type.getRepairPart() != VehicleRepairPart.TRUNK) continue;
            if (!this.vehicle.getParts().containsKey(type)) {
                this.partsPane.addItem(new GuiItem(VehiclesConfig.GUI.NO_PART_ITEMS.get().get(type).build()));
                continue;
            }

            final VehicleItemHolder holder = this.vehicle.getParts().get(type);
            final VehiclePart vehiclePart = holder.getVehiclePart();
            final ItemStack itemStack = vehiclePart.getItemStack(holder.getDurability());
            this.partsPane.addItem(new GuiItem(itemStack, event -> {
                this.vehicle.getParts().remove(type);

                final Player player = (Player) event.getWhoClicked();

                player.getInventory().addItem(itemStack);
                this.redraw(event);
            }));
        }

        this.addPane(this.partsPane);

        this.setOnBottomClick(event -> {
            final ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null || itemStack.getType() == Material.AIR) return;

            final VehiclePart vehiclePart = VehiclePart.fromItem(itemStack);
            if (vehiclePart == null) return;

            if (vehiclePart.getItemType().getRepairPart() != VehicleRepairPart.TRUNK) return;
            if (this.vehicle.getParts().containsKey(vehiclePart.getItemType())) return;

            if (VehiclePart.getDurability(itemStack) <= 0) return;

            this.vehicle.getParts().put(vehiclePart.getItemType(), new VehicleItemHolder(vehiclePart, VehiclePart.getDurability(itemStack)));
            itemStack.setAmount(itemStack.getAmount() - 1);
            this.vehicle.createBaseModel();
            this.redraw(event);
        });
    }

    private void redraw(InventoryClickEvent event) {
        WegaCars.INSTANCE.getTaskScheduler().schedule(Tasks.sync(WegaCars.INSTANCE, ctx ->
                new TrunkVehicleGUI(this.vehicle).show(event.getWhoClicked())));
    }
}
