package me.wega.cars.vehicle.gui;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import me.wega.cars.WegaCars;
import me.wega.cars.VehiclesConfig;
import me.wega.cars.VehiclesMessages;
import me.wega.cars.item.part.VehiclePart;
import me.wega.cars.item.part.VehiclePartType;
import me.wega.cars.item.part.impl.VehicleTurbo;
import me.wega.cars.item.part.minigames.EngineRemovalMinigame;
import me.wega.cars.item.part.minigames.EngineSetMinigame;
import me.wega.cars.item.part.minigames.SuspensionReplacementMinigame;
import me.wega.cars.item.part.minigames.TuneupMinigame;
import me.wega.cars.item.suspension.VehicleSuspension;
import me.wega.cars.vehicle.Vehicle;
import me.wega.cars.vehicle.holder.VehicleItemHolder;
import me.wega.cars.toolkit.task.Tasks;
import me.wega.cars.toolkit.utils.ColorUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.function.Consumer;

public class EngineVehicleGUI extends ChestGui {

    private final @NotNull Vehicle vehicle;
    private final OutlinePane partsPane = new OutlinePane(1, 1, 7, 1);
    public EngineVehicleGUI(@NotNull Vehicle vehicle) {
        super(3, ComponentHolder.of(ColorUtils.color(VehiclesConfig.Titles.ENGINE_PARTS_TITLE.get())));
        this.vehicle = vehicle;
        this.initialize();
    }

    private void initialize() {
        this.setOnGlobalClick(event -> event.setCancelled(true));

        this.drawEngine();
        this.drawSuspension();
        this.drawTurbo();
        this.drawTuneup();

        this.addPane(this.partsPane);

        this.setOnBottomClick(event -> {
            final Player player = (Player) event.getWhoClicked();
            final ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null || itemStack.getType() == Material.AIR) return;

            final VehicleSuspension suspension = VehicleSuspension.fromItem(itemStack);
            if (suspension != null) {
                WegaCars.INSTANCE.getTaskScheduler().schedule(Tasks.sync(WegaCars.INSTANCE, ctx ->
                        new SuspensionReplacementMinigame(vehicle, suspension).show(event.getWhoClicked())));
                itemStack.setAmount(itemStack.getAmount() - 1);
                return;
            }

            final VehiclePart vehiclePart = VehiclePart.fromItem(itemStack);
            if (vehiclePart == null) return;

            if (vehiclePart.getItemType() != VehiclePartType.ENGINE && vehiclePart.getItemType() != VehiclePartType.TURBO) return;
            if (this.vehicle.getParts().containsKey(vehiclePart.getItemType())) return;

            final int durability = VehiclePart.getDurability(itemStack);
            if (durability <= 0) return;

            if (vehiclePart.getItemType() == VehiclePartType.ENGINE) {
                if (vehicle.getEngineSetTimer() > Instant.now().getEpochSecond()) {
                    player.closeInventory();
                    player.sendMessage(ColorUtils.color(VehiclesMessages.ENGINE_NOT_SET_YET.get()));
                    return;
                }

                WegaCars.INSTANCE.getTaskScheduler().schedule(Tasks.sync(WegaCars.INSTANCE, ctx ->
                        new EngineSetMinigame(this.vehicle, new VehicleItemHolder(vehiclePart, durability)).show(event.getWhoClicked())));
                itemStack.setAmount(itemStack.getAmount() - 1);
                return;
            }

            if (vehiclePart.getItemType() == VehiclePartType.TURBO) {
                final VehicleTurbo turbo = (VehicleTurbo) vehiclePart;
                if (turbo.getType() != this.vehicle.getVehicleType().getTurboType()) return;

                final Consumer<Integer> success = turns -> {
                    this.vehicle.getParts().put(vehiclePart.getItemType(), new VehicleItemHolder(vehiclePart, VehiclePart.getDurability(itemStack)));
                    itemStack.setAmount(itemStack.getAmount() - 1);
                    this.vehicle.createBaseModel();
                };

                WegaCars.INSTANCE.getTaskScheduler().schedule(Tasks.sync(WegaCars.INSTANCE, ctx ->
                        new TuneupMinigame(success).show(event.getWhoClicked())));
            }
        });
    }

    private void drawTuneup() {
        if (this.vehicle.getParts().containsKey(VehiclePartType.ENGINE)) {
            if (this.vehicle.getDriveTime() < VehiclesConfig.Minigame.MINIMUM_DRIVE_TIME.get()) return;

            final Consumer<Integer> success = turns -> {
                final double factor = (double) turns / VehiclesConfig.Minigame.MAX_TURNS.get();
                final double repairFactor = VehiclesConfig.Minigame.MAX_REPAIR_FACTOR.get() * factor;
                final VehicleItemHolder holder = this.vehicle.getParts().get(VehiclePartType.ENGINE);

                final int durabilityToRepair = (int) (holder.getDurability() * repairFactor);

                holder.setDurability(Math.min(holder.getVehiclePart().getMaxDurability(), holder.getDurability() + durabilityToRepair));
                this.vehicle.setDriveTime(0);
            };


            this.partsPane.addItem(new GuiItem(VehiclesConfig.GUI.TUNE_UP_ITEM.get().build(), event ->
                    WegaCars.INSTANCE.getTaskScheduler().schedule(Tasks.sync(WegaCars.INSTANCE, ctx ->
                            new TuneupMinigame(success).show(event.getWhoClicked())))));
        }
    }

    private void drawEngine() {
        if (!this.vehicle.getParts().containsKey(VehiclePartType.ENGINE)) {
            this.partsPane.addItem(new GuiItem(VehiclesConfig.GUI.NO_PART_ITEMS.get().get(VehiclePartType.ENGINE).build()));
            return;
        }

        final VehicleItemHolder holder = this.vehicle.getParts().get(VehiclePartType.ENGINE);
        final VehiclePart vehiclePart = holder.getVehiclePart();
        final ItemStack itemStack = vehiclePart.getItemStack(holder.getDurability());

        this.partsPane.addItem(new GuiItem(itemStack, event ->
                new EngineRemovalMinigame(this.vehicle).show(event.getWhoClicked())));
    }

    private void drawTurbo() {
        if (!this.vehicle.getParts().containsKey(VehiclePartType.TURBO)) {
            this.partsPane.addItem(new GuiItem(VehiclesConfig.GUI.NO_PART_ITEMS.get().get(VehiclePartType.TURBO).build()));
            return;
        }

        final VehicleItemHolder holder = this.vehicle.getParts().get(VehiclePartType.TURBO);
        final VehiclePart vehiclePart = holder.getVehiclePart();
        final ItemStack itemStack = vehiclePart.getItemStack(holder.getDurability());

        this.partsPane.addItem(new GuiItem(itemStack, event -> {
            this.vehicle.getParts().remove(VehiclePartType.TURBO);

            final Player player = (Player) event.getWhoClicked();

            player.getInventory().addItem(itemStack);
            this.redraw(event);
        }));
    }

    private void drawSuspension() {
        if (this.vehicle.getSuspension() == null)
            this.partsPane.addItem(new GuiItem(VehiclesConfig.GUI.NO_SUSPENSION_ITEM.get().build()));
        else {
            final ItemStack suspension = this.vehicle.getSuspension().createItem();
            this.partsPane.addItem(new GuiItem(suspension, event -> {
                this.vehicle.setSuspension(null);

                final Player player = (Player) event.getWhoClicked();
                player.getInventory().addItem(suspension);
                this.redraw(event);
            }));
        }
    }


    private void redraw(InventoryClickEvent event) {
        WegaCars.INSTANCE.getTaskScheduler().schedule(Tasks.sync(WegaCars.INSTANCE, ctx ->
                new EngineVehicleGUI(this.vehicle).show(event.getWhoClicked())));
    }
}
