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
import me.wega.cars.vehicle.holder.VehicleItemHolder;
import me.wega.cars.toolkit.task.Tasks;
import me.wega.cars.toolkit.utils.ColorUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SparkplugVehicleGUI extends ChestGui {

    private final @NotNull Vehicle vehicle;
    private final OutlinePane partsPane = new OutlinePane(1, 1, 7, 1);
    public SparkplugVehicleGUI(@NotNull Vehicle vehicle) {
        super(3, ComponentHolder.of(ColorUtils.color(VehiclesConfig.Titles.SPARKPLUG_PARTS_TITLE.get())));
        this.vehicle = vehicle;
        this.initialize();
    }

    private void initialize() {
        this.setOnGlobalClick(event -> event.setCancelled(true));

        this.drawSparkplug();

        this.addPane(this.partsPane);

        this.setOnBottomClick(event -> {
            final ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null || itemStack.getType() == Material.AIR) return;

            final VehiclePart vehiclePart = VehiclePart.fromItem(itemStack);
            if (vehiclePart == null) return;

            if (vehiclePart.getItemType() != VehiclePartType.SPARKPLUG) return;
            if (this.vehicle.getParts().containsKey(vehiclePart.getItemType())) return;

            final int durability = VehiclePart.getDurability(itemStack);
            if (durability <= 0) return;

            if (this.vehicle.getFirstSparkplug() == null)
                this.vehicle.setFirstSparkplug(vehiclePart.getItemId());

            this.vehicle.getParts().put(vehiclePart.getItemType(), new VehicleItemHolder(vehiclePart, durability));
            itemStack.setAmount(itemStack.getAmount() - 1);
            this.vehicle.createBaseModel();
            this.redraw(event);
        });
    }


    private void drawSparkplug() {
        if (!this.vehicle.getParts().containsKey(VehiclePartType.SPARKPLUG)) {
            this.partsPane.addItem(new GuiItem(VehiclesConfig.GUI.NO_PART_ITEMS.get().get(VehiclePartType.SPARKPLUG).build()));
            return;
        }

        final VehicleItemHolder holder = this.vehicle.getParts().get(VehiclePartType.SPARKPLUG);
        final VehiclePart vehiclePart = holder.getVehiclePart();
        final ItemStack itemStack = vehiclePart.getItemStack(holder.getDurability());

        this.partsPane.addItem(new GuiItem(itemStack, event -> {
            this.vehicle.getParts().remove(VehiclePartType.SPARKPLUG);

            final Player player = (Player) event.getWhoClicked();

            player.getInventory().addItem(itemStack);
            this.redraw(event);
        }));
    }

    private void redraw(InventoryClickEvent event) {
        WegaCars.INSTANCE.getTaskScheduler().schedule(Tasks.sync(WegaCars.INSTANCE, ctx ->
                new SparkplugVehicleGUI(this.vehicle).show(event.getWhoClicked())));
    }
}
