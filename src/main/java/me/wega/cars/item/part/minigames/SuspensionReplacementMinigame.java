package me.wega.cars.item.part.minigames;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import me.wega.cars.WegaCars;
import me.wega.cars.VehiclesConfig;
import me.wega.cars.VehiclesMessages;
import me.wega.cars.item.suspension.VehicleSuspension;
import me.wega.cars.toolkit.config.sound.ConfigSounds;
import me.wega.cars.vehicle.Vehicle;
import me.wega.cars.toolkit.utils.ColorUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SuspensionReplacementMinigame extends ChestGui {

    private static final ConfigSounds SOUND = WegaCars.INSTANCE.getSounds();

    private final @NotNull Vehicle vehicle;
    private final @NotNull VehicleSuspension suspension;
    private int boltsRemoved = 0;
    private boolean giveItemBack = true;

    private final Map<Slot, Integer> clicked = new HashMap<>();
    private final StaticPane boltsPane = new StaticPane(0, 0, 9, 5);

    public SuspensionReplacementMinigame(@NotNull Vehicle vehicle, @NotNull VehicleSuspension suspension) {
        super(5, ComponentHolder.of(ColorUtils.color(VehiclesConfig.Titles.FIRST_SUSPENSION_REPLACEMENT_MINIGAME_TITLE.get())));
        this.vehicle = vehicle;
        this.suspension = suspension;
        this.initialize();
    }

    private void initialize() {
        this.setOnGlobalClick(event -> event.setCancelled(true));
        this.setOnClose(event -> {
            if (this.giveItemBack)
                event.getPlayer().getInventory().addItem(this.suspension.createItem());
        });

        final int[][] slots = {{2, 2, 0}, {4, 2, 1}, {6, 2, 0}, {4, 0, 0}}; // X, Y, and 1 if it is the center bolt
        for (int[] slot : slots) {
            this.boltsPane.addItem(new GuiItem(VehiclesConfig.BOLT_ITEM.get().clone().build(), event -> {
                final Player player = (Player) event.getWhoClicked();
                if (slot[2] == 1) {
                    this.giveItemBack = false;
                    player.closeInventory();
                    player.sendMessage(ColorUtils.color(VehiclesMessages.SUSPENSION_DESTROYED.get()));
                    SOUND.get("suspension-failure").play(player);
                    return;
                }

                this.boltsRemoved++;
                this.boltsPane.removeItem(slot[0], slot[1]);
                SOUND.get("bolt-removed").play(player);
                if (this.boltsRemoved == 3)
                    this.initializeStageTwo();

                this.update();
            }), slot[0], slot[1]);
        }

        this.addPane(this.boltsPane);
    }

    private void initializeStageTwo() {
        this.boltsPane.clear();
        this.boltsRemoved = 0;
        this.setTitle(ComponentHolder.of(ColorUtils.color(VehiclesConfig.Titles.SECOND_SUSPENSION_REPLACEMENT_MINIGAME_TITLE.get())));
        final int[][] slots = {{2, 3}, {4, 3}, {6, 3}};
        for (int[] slot : slots) {
            final Slot slotObject = Slot.fromXY(slot[0], slot[1]);
            this.boltsPane.addItem(new GuiItem(VehiclesConfig.BOLT_ITEM.get().clone().build(), event -> {
                final Player player = (Player) event.getWhoClicked();

                int timesClicked = this.clicked.getOrDefault(slotObject, 0);
                timesClicked++;
                SOUND.get("bolt-click").play(player);
                if (timesClicked == 10) {
                    this.boltsRemoved++;
                    this.boltsPane.removeItem(slotObject);
                    if (this.boltsRemoved == 3) {
                        this.vehicle.setSuspension(this.suspension);
                        this.giveItemBack = false;
                        player.closeInventory();
                        player.sendMessage(ColorUtils.color(VehiclesMessages.MINIGAME_SUCCESSFUL.get()));
                        SOUND.get("suspension-success").play(player);
                    }
                }
                this.clicked.put(slotObject, timesClicked);
                this.update();
            }), slotObject);
        }
    }
}
