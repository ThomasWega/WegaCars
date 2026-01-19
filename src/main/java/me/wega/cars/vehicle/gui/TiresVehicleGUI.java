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
import me.wega.cars.vehicle.enums.VehicleWheelPart;
import me.wega.cars.vehicle.holder.VehicleItemHolder;
import me.wega.cars.vehicle.holder.VehicleWheelHolder;
import me.wega.cars.toolkit.task.Tasks;
import me.wega.cars.toolkit.utils.ColorUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TiresVehicleGUI extends ChestGui {

    private final @NotNull Vehicle vehicle;
    private final @NotNull VehicleWheelHolder holder;

    private final OutlinePane partsPane = new OutlinePane(1, 1, 7, 1);

    public TiresVehicleGUI(@NotNull Vehicle vehicle, @NotNull VehicleWheelHolder holder) {
        super(3, ComponentHolder.of(ColorUtils.color(VehiclesConfig.Titles.TIRE_PARTS_TITLE.get())));
        this.vehicle = vehicle;
        this.holder = holder;
        this.initialize();
    }

    private void initialize() {
        this.setOnGlobalClick(event -> event.setCancelled(true));

        for (VehicleWheelPart wheelPart : VehicleWheelPart.values()) {
            if (wheelPart == VehicleWheelPart.TIRE) continue;
            if (!this.holder.getWheelParts().containsKey(wheelPart)) {
                this.partsPane.addItem(new GuiItem(VehiclesConfig.GUI.NO_PART_ITEMS.get().get(wheelPart.getVehiclePartType()).build()));
                continue;
            }

            final VehicleItemHolder itemHolder = this.holder.getWheelParts().get(wheelPart);
            final ItemStack itemStack = itemHolder.getVehiclePart().getItemStack(itemHolder.getDurability());
            this.partsPane.addItem(new GuiItem(itemStack, event -> {
                this.holder.getWheelParts().remove(wheelPart);

                final Player player = (Player) event.getWhoClicked();
                player.getInventory().addItem(itemStack);
                this.redraw(event);
            }));
        }

        this.partsPane.addItem(new GuiItem(VehiclesConfig.GUI.ALIGNMENT_COMBINATION.get().clone()
                .addTagResolvers(
                        Placeholder.parsed("alignment", this.holder.getAlignmentCombination().name())
                )
                .build(),
                event -> new AlignmentSelectionGUI(this.vehicle, this.holder).show(event.getWhoClicked())
        ));

        this.addPane(this.partsPane);

        this.setOnBottomClick(event -> {
            final ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null || itemStack.getType() == Material.AIR) return;

            final VehiclePart vehiclePart = VehiclePart.fromItem(itemStack);
            if (vehiclePart == null) return;

            if (vehiclePart.getItemType() == VehiclePartType.BRAKES) {
                if (VehiclePart.getDurability(itemStack) <= 0) return;

                if (this.holder.getWheelParts().containsKey(VehicleWheelPart.BRAKE)) return;

                this.holder.getWheelParts().put(VehicleWheelPart.BRAKE, new VehicleItemHolder(vehiclePart, VehiclePart.getDurability(itemStack)));
                itemStack.setAmount(itemStack.getAmount() - 1);
                this.vehicle.createBaseModel();
                this.redraw(event);
            }
        });
    }

    private void redraw(InventoryClickEvent event) {
        WegaCars.INSTANCE.getTaskScheduler().schedule(Tasks.sync(WegaCars.INSTANCE, ctx ->
                new TiresVehicleGUI(this.vehicle, this.holder).show(event.getWhoClicked())));
    }
}
