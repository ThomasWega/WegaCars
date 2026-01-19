package me.wega.cars.vehicle.gui;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import me.wega.cars.VehiclesConfig;
import me.wega.cars.item.part.minigames.SetAlignmentMinigame;
import me.wega.cars.vehicle.Vehicle;
import me.wega.cars.vehicle.enums.VehicleAlignmentCombination;
import me.wega.cars.vehicle.holder.VehicleWheelHolder;
import me.wega.cars.toolkit.utils.ColorUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;

public class AlignmentSelectionGUI extends ChestGui {

    private final @NotNull Vehicle vehicle;
    private final @NotNull VehicleWheelHolder holder;

    private final OutlinePane partsPane = new OutlinePane(0, 1, 9, 1);

    public AlignmentSelectionGUI(@NotNull Vehicle vehicle, @NotNull VehicleWheelHolder holder) {
        super(3, ComponentHolder.of(ColorUtils.color(VehiclesConfig.Titles.ALIGNMENT_SELECTION_TITLE.get())));
        this.vehicle = vehicle;
        this.holder = holder;
        this.initialize();
    }

    private void initialize() {
        this.setOnGlobalClick(event -> event.setCancelled(true));

        for (VehicleAlignmentCombination combination : VehicleAlignmentCombination.values()) {
            this.partsPane.addItem(new GuiItem(
                    VehiclesConfig.GUI.TIRE_ALIGNMENT_SELECTION.get().clone()
                            .addTagResolvers(
                                    Placeholder.parsed("alignment", combination.getName())
                            )
                            .build(),
                    event -> new SetAlignmentMinigame(this.vehicle, this.holder, combination).show(event.getWhoClicked())
            ));
        }

        this.addPane(this.partsPane);
    }
}
