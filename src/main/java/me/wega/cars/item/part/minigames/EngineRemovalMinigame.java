package me.wega.cars.item.part.minigames;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import me.wega.cars.WegaCars;
import me.wega.cars.VehiclesConfig;
import me.wega.cars.VehiclesMessages;
import me.wega.cars.item.part.VehiclePartType;
import me.wega.cars.toolkit.config.sound.ConfigSounds;
import me.wega.cars.vehicle.Vehicle;
import me.wega.cars.toolkit.utils.ColorUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class EngineRemovalMinigame extends ChestGui {

    private static final ConfigSounds SOUND = WegaCars.INSTANCE.getSounds();

    private final @NotNull Vehicle vehicle;
    private int boltsRemoved = 0;

    private final Map<Slot, Integer> clicked = new HashMap<>();
    private final StaticPane boltsPane = new StaticPane(0, 0, 9, 5);

    public EngineRemovalMinigame(@NotNull Vehicle vehicle) {
        super(5, ComponentHolder.of(ColorUtils.color(VehiclesConfig.Titles.ENGINE_REMOVAL_MINIGAME_TITLE.get())));
        this.vehicle = vehicle;
        this.initialize();
    }

    private void initialize() {
        this.setOnGlobalClick(event -> event.setCancelled(true));

        final int[][] slots = {
                {1, 1}, {3, 1}, {5, 1}, {7, 1},
                {1, 2}, {3, 2}, {5, 2}, {7, 2},
                {1, 3}, {3, 3}, {5, 3}, {7, 3}
        }; // X, Y

        for (int[] slot : slots) {
            final Slot slotObject = Slot.fromXY(slot[0], slot[1]);
            this.boltsPane.addItem(new GuiItem(VehiclesConfig.BOLT_ITEM.get().clone().build(), event -> {
                final Player player = (Player) event.getWhoClicked();

                int timesClicked = this.clicked.getOrDefault(slotObject, 0);
                timesClicked++;
                SOUND.get("bolt-click").play(player);
                if (timesClicked == 3) {
                    this.boltsRemoved++;
                    this.boltsPane.removeItem(slotObject);
                    if (this.boltsRemoved == 12) {
                        player.getInventory().addItem(this.vehicle.getParts().get(VehiclePartType.ENGINE).getItemStack());

                        this.vehicle.getParts().remove(VehiclePartType.ENGINE);
                        this.vehicle.setEngineSetTimer(Instant.now().getEpochSecond() + VehiclesConfig.ENGINE_SET_TIME.get());
                        player.closeInventory();
                        player.sendMessage(ColorUtils.color(VehiclesMessages.MINIGAME_SUCCESSFUL.get()));

                        SOUND.get("engine-removed").play(player);

                        return;
                    }
                }
                this.clicked.put(slotObject, timesClicked);
                this.update();
            }), slotObject);
        }

        this.addPane(this.boltsPane);
    }
}
