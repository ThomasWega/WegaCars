package me.wega.cars.toolkit.gui.button;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import me.wega.cars.toolkit.config.impl.GUIItemsConfig;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CloseButton extends GuiItem {

    public CloseButton() {
        this(null);
    }

    public CloseButton(@Nullable Consumer<InventoryClickEvent> action) {
        super(GUIItemsConfig.Navigation.CLOSE.get().builder().hideFlags().build(), action);
    }
}
