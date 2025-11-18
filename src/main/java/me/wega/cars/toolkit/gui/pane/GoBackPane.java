package me.wega.cars.toolkit.gui.pane;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.wega.cars.WegaCars;
import me.wega.cars.toolkit.gui.button.GoBackButton;
import me.wega.cars.toolkit.task.TaskScheduler;
import me.wega.cars.toolkit.task.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A pane that contains a go back button.
 */
public class GoBackPane extends StaticPane {
    private final TaskScheduler taskScheduler = WegaCars.INSTANCE.getTaskScheduler();

    public GoBackPane(int x, int y, @Nullable Gui previousGui) {
        super(x, y, 1, 1, Priority.HIGHEST);

        GuiItem backItem = new GoBackButton(event -> {
            Player player = ((Player) event.getWhoClicked());
            event.setCancelled(true);
            if (previousGui == null) {
                taskScheduler.schedule(Tasks.sync(WegaCars.INSTANCE, ctx ->
                        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
                ));
                return;
            }
            taskScheduler.schedule(Tasks.sync(WegaCars.INSTANCE, ctx ->
                    previousGui.show(player)
            ));
        });
        this.addItem(backItem, 0, 0);
    }

    public GoBackPane(int x, int y, @Nullable Consumer<InventoryClickEvent> action) {
        super(x, y, 1, 1, Priority.HIGHEST);
        this.addItem(new GoBackButton(action), 0, 0);
    }
}
