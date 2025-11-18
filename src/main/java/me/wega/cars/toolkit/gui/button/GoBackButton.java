package me.wega.cars.toolkit.gui.button;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import me.wega.cars.toolkit.config.impl.GUIItemsConfig;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class GoBackButton extends GuiItem {

    public GoBackButton() {
        this(null);
    }

    public GoBackButton(@Nullable Consumer<InventoryClickEvent> action) {
        super(GUIItemsConfig.Navigation.GO_BACK.get().builder().hideFlags().build(), action);
    }
}
