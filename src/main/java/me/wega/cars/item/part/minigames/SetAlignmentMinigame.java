package me.wega.cars.item.part.minigames;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import me.wega.cars.WegaCars;
import me.wega.cars.VehiclesConfig;
import me.wega.cars.VehiclesMessages;
import me.wega.cars.vehicle.Vehicle;
import me.wega.cars.vehicle.enums.VehicleAlignmentCombination;
import me.wega.cars.vehicle.holder.VehicleWheelHolder;
import me.wega.cars.toolkit.builder.ItemBuilder;
import me.wega.cars.toolkit.task.Tasks;
import me.wega.cars.toolkit.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class SetAlignmentMinigame extends ChestGui {

    private static final ItemStack FILLER_ITEM = new ItemBuilder(Material.WHITE_WOOL)
            .displayName(Component.empty())
            .hideFlags()
            .build();
    private static final int REQUIRED_ITEMS = 8;

    private final StaticPane fillerPane = new StaticPane(0, 0, 9, 6);
    private final StaticPane tempPane = new StaticPane(0, 0, 9, 6, Pane.Priority.HIGH);

    private final @NotNull Vehicle vehicle;
    private final @NotNull VehicleWheelHolder wheelHolder;
    private final @NotNull VehicleAlignmentCombination combination;

    private int clickCount = 0;

    public SetAlignmentMinigame(@NotNull Vehicle vehicle, @NotNull VehicleWheelHolder wheelHolder, @NotNull VehicleAlignmentCombination combination) {
        super(6, ComponentHolder.of(ColorUtils.color(VehiclesConfig.Titles.SET_ALIGNMENT_MINIGAME_TITLE.get())));
        this.vehicle = vehicle;
        this.wheelHolder = wheelHolder;
        this.combination = combination;
        this.initialize();
    }

    private void initialize() {
        this.fillerPane.fillWith(FILLER_ITEM);

        this.setTempItems();

        this.addPane(this.fillerPane);
        this.addPane(this.tempPane);

        this.setOnGlobalClick(event -> event.setCancelled(true));
        this.setOnGlobalDrag(event -> event.setCancelled(true));
    }

    private void setTempItems() {
        final Set<Slot> randomSlots = new HashSet<>();
        for (int i = 0; i < REQUIRED_ITEMS; i++) {
            final Slot slot = Slot.fromIndex(ThreadLocalRandom.current().nextInt(this.getRows() * 9));
            if (randomSlots.contains(slot)) {
                i--;
                continue;
            }

            randomSlots.add(slot);
        }

        randomSlots.forEach(slot -> this.tempPane.addItem(new GuiItem(new ItemBuilder(Material.LIME_WOOL)
                .displayName(ColorUtils.color("<green>Click on me when I disappear!"))
                .hideFlags()
                .build()), slot));

        WegaCars.INSTANCE.getTaskScheduler().schedule(Tasks.later(WegaCars.INSTANCE, 100L, ctx -> {
            this.fillerPane.clear();
            this.fillerPane.fillWith(FILLER_ITEM, event -> this.failure((Player) event.getWhoClicked()));

            randomSlots.forEach(slot -> this.tempPane.addItem(new GuiItem(FILLER_ITEM, event -> {
                this.tempPane.addItem(new GuiItem(new ItemBuilder(Material.BLACK_WOOL)
                        .displayName(ColorUtils.color("<black><obf>Done"))
                        .hideFlags()
                        .build()), slot);

                this.clickCount += 1;
                if (this.clickCount == REQUIRED_ITEMS) {
                    this.success((Player) event.getWhoClicked());
                    return;
                }

                this.update();
            }), slot));

            this.update();
        }));
    }

    private void success(@NotNull Player player) {
        player.sendMessage(ColorUtils.color(VehiclesMessages.COMBINATION_SET.get()));
        player.closeInventory();

        this.wheelHolder.setAlignmentCombination(this.combination);
        this.vehicle.createBaseModel();
    }

    private void failure(@NotNull Player player) {
        player.sendMessage(ColorUtils.color(VehiclesMessages.COMBINATION_FAILURE.get()));
        player.closeInventory();
    }
}
