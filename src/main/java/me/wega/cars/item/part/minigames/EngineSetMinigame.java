package me.wega.cars.item.part.minigames;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.wega.cars.VehiclesConfig;
import me.wega.cars.VehiclesMessages;
import me.wega.cars.item.part.VehiclePartType;
import me.wega.cars.vehicle.Vehicle;
import me.wega.cars.vehicle.holder.VehicleItemHolder;
import me.wega.cars.toolkit.builder.ItemBuilder;
import me.wega.cars.toolkit.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EngineSetMinigame extends ChestGui {

    private final @NotNull Vehicle vehicle;
    private final @NotNull VehicleItemHolder holder;

    private final @NotNull List<Material> correctList = List.of(Material.BLUE_CANDLE, Material.LIME_CANDLE, Material.WHITE_CANDLE,
            Material.BLUE_WOOL, Material.LIME_WOOL, Material.WHITE_WOOL);

    private final @NotNull List<Material> toSort = new ArrayList<>(correctList);

    private final @NotNull List<Material> bottomList = List.of(Material.BLUE_CONCRETE, Material.LIME_CONCRETE, Material.WHITE_CONCRETE);

    private final OutlinePane pane = new OutlinePane(3, 0, 3, 3);
    private final StaticPane optionsPane = new StaticPane(1, 4, 7, 1);

    public EngineSetMinigame(@NotNull Vehicle vehicle, @NotNull VehicleItemHolder holder) {
        super(6, ComponentHolder.of(ColorUtils.color(VehiclesConfig.Titles.ENGINE_SET_MINIGAME_TITLE.get())));
        this.vehicle = vehicle;
        this.holder = holder;

        this.initialize();
    }

    private void randomize() {
        Collections.shuffle(this.toSort);
        if (this.toSort.equals(this.correctList)) this.randomize();
    }

    private void initialize() {
        this.setOnGlobalClick(event -> event.setCancelled(true));

        this.randomize();
        this.refreshPane();

        this.optionsPane.addItem(new GuiItem(VehiclesConfig.Minigame.ROTATE_COLUMN_1_ITEM.get().build(), event -> {
            Collections.swap(this.toSort, 0, 3);
            this.refreshPane();
        }), 0, 0);

        this.optionsPane.addItem(new GuiItem(VehiclesConfig.Minigame.ROTATE_ROW_1_ITEM.get().build(), event -> {
            Collections.swap(this.toSort, 0, 1);
            Collections.swap(this.toSort, 0, 2);
            this.refreshPane();
        }), 1, 0);

        this.optionsPane.addItem(new GuiItem(VehiclesConfig.Minigame.MINIGAME_INFO_ITEM.get().build()), 2, 0);

        this.optionsPane.addItem(new GuiItem(VehiclesConfig.Minigame.ROTATE_COLUMN_2_ITEM.get().build(), event -> {
            Collections.swap(this.toSort, 1, 4);
            this.refreshPane();
        }), 4, 0);

        this.optionsPane.addItem(new GuiItem(VehiclesConfig.Minigame.ROTATE_COLUMN_3_ITEM.get().build(), event -> {
            Collections.swap(this.toSort, 2, 5);
            this.refreshPane();
        }), 5, 0);

        this.optionsPane.addItem(new GuiItem(VehiclesConfig.Minigame.CONNECT_ITEM.get().build(), this::completeMinigame),
                6, 0);

        this.addPane(this.pane);
        this.addPane(this.optionsPane);
    }

    private void refreshPane() {
        this.pane.clear();
        for (Material material : this.toSort) {
            final ItemStack itemStack = new ItemBuilder(material)
                    .displayName(Component.empty())
                    .build();

            this.pane.addItem(new GuiItem(itemStack));
        }

        for (Material material : this.bottomList) {
            final ItemStack itemStack = new ItemBuilder(material)
                    .displayName(ColorUtils.color("<green>Base Item"))
                    .build();

            this.pane.addItem(new GuiItem(itemStack));
        }

        this.update();
    }

    private void completeMinigame(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        player.closeInventory();
        if (this.toSort.equals(this.correctList)) {
            this.vehicle.getParts().put(VehiclePartType.ENGINE, this.holder);
            player.sendMessage(ColorUtils.color(VehiclesMessages.MINIGAME_SUCCESSFUL.get()));
            return;
        }

        this.holder.setDurability(this.holder.getDurability() / 2);
        player.getInventory().addItem(this.holder.getItemStack());
        player.sendMessage(ColorUtils.color(VehiclesMessages.MINIGAME_FAILED.get()));
    }
}
