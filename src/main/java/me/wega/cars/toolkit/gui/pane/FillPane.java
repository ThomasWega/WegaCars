package me.wega.cars.toolkit.gui.pane;

import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.wega.cars.toolkit.builder.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A pane that fills the pane with a specific item.
 */
public class FillPane extends StaticPane {

    public FillPane(int x, int y, int length, int height, @NotNull Material material) {
        super(x, y, length, height, Priority.LOWEST);
        this.fillWith(new ItemBuilder(material)
                        .displayName(Component.empty())
                        .hideFlags()
                        .build(),
                event -> event.setCancelled(true));
    }

    public FillPane(int x, int y, int length, int height, @NotNull ItemStack item) {
        super(x, y, length, height, Priority.LOWEST);
        this.fillWith(item, event -> event.setCancelled(true));
    }
}
