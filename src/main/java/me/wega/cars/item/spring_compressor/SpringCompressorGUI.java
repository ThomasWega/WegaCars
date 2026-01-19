package me.wega.cars.item.spring_compressor;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.wega.cars.WegaCars;
import me.wega.cars.VehiclesConfig;
import me.wega.cars.item.part.VehiclePart;
import me.wega.cars.item.part.VehiclePartType;
import me.wega.cars.item.suspension.VehicleSuspension;
import me.wega.cars.toolkit.task.Tasks;
import me.wega.cars.toolkit.utils.ColorUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SpringCompressorGUI extends ChestGui {

    private static final int SPRING_INDEX = 0;
    private static final int SHOCK_INDEX = 1;

    private boolean success;

    private final StaticPane pane = new StaticPane(0, 0, 9, 4);
    private final VehiclePart[] items;

    public SpringCompressorGUI() {
        this(new VehiclePart[2]);
    }

    public SpringCompressorGUI(@NotNull VehiclePart[] items) {
        super(3, ComponentHolder.of(ColorUtils.color(VehiclesConfig.Titles.SPRING_COMPRESSOR_TITLE.get())));
        this.items = items;
        this.initialize();
    }

    private void initialize() {
        this.setOnGlobalClick(event -> event.setCancelled(true));

        this.showItem(VehiclePartType.SPRING, SPRING_INDEX, 2, 1);
        this.showItem(VehiclePartType.SHOCK, SHOCK_INDEX, 6, 1);

        this.pane.addItem(new GuiItem(VehiclesConfig.GUI.CREATE_SUSPENSION_ITEM.get().build(), event -> {
            if (this.items[SPRING_INDEX] == null || this.items[SHOCK_INDEX] == null) return;

            final VehicleSuspension suspension = new VehicleSuspension(this.items[SPRING_INDEX], this.items[SHOCK_INDEX]);
            final Player player = (Player) event.getWhoClicked();

            player.getInventory().addItem(suspension.createItem());
            this.success = true;
            player.closeInventory();
        }), 4, 1);

        this.addPane(this.pane);

        this.setOnClose(event -> {
            final Player player = (Player) event.getPlayer();
            if (!this.success) {
                if (this.items[SPRING_INDEX] != null)
                    player.getInventory().addItem(this.items[SPRING_INDEX].getNewItemStack());

                if (this.items[SHOCK_INDEX] != null)
                    player.getInventory().addItem(this.items[SHOCK_INDEX].getNewItemStack());
            }
        });

        this.setOnBottomClick(event -> {
            final ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null || itemStack.getType() == Material.AIR) return;

            final VehiclePart vehiclePart = VehiclePart.fromItem(itemStack);
            if (vehiclePart == null) return;

            if (vehiclePart.getItemType() == VehiclePartType.SPRING) {
                itemStack.setAmount(itemStack.getAmount() - 1);
                this.items[SPRING_INDEX] = vehiclePart;
                this.redraw(event);
                return;
            }

            if (vehiclePart.getItemType() == VehiclePartType.SHOCK) {
                itemStack.setAmount(itemStack.getAmount() - 1);
                this.items[SHOCK_INDEX] = vehiclePart;
                this.redraw(event);
            }
        });
    }

    private void showItem(VehiclePartType type, int index, int x, int y) {
        final VehiclePart vehiclePart = this.items[index];
        if (vehiclePart != null)
            this.pane.addItem(new GuiItem(vehiclePart.getNewItemStack(), event -> {
                final Player player = (Player) event.getWhoClicked();

                player.getInventory().addItem(vehiclePart.getNewItemStack());
                this.items[index] = null;
                this.redraw(event);
            }), x, y);
        else {
            this.pane.addItem(new GuiItem(VehiclesConfig.GUI.NO_PART_ITEMS.get().get(type).build()), x, y);
        }
    }

    private void redraw(InventoryClickEvent event) {
        this.success = true;
        WegaCars.INSTANCE.getTaskScheduler().schedule(Tasks.sync(WegaCars.INSTANCE, ctx ->
                new SpringCompressorGUI(this.items).show(event.getWhoClicked())));
    }
}
