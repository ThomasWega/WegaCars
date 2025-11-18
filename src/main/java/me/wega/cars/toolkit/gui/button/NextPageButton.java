package me.wega.cars.toolkit.gui.button;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import me.wega.cars.toolkit.config.impl.GUIItemsConfig;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class NextPageButton extends GuiItem {

    public NextPageButton() {
        this(null);
    }

    public NextPageButton(@Nullable Consumer<InventoryClickEvent> action) {
        super(GUIItemsConfig.Navigation.NEXT_PAGE.get().builder().hideFlags().build(), action);
    }
}
