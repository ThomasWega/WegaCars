package me.wega.cars.toolkit.gui.pane;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.wega.cars.WegaCars;
import me.wega.cars.toolkit.gui.button.CloseButton;
import me.wega.cars.toolkit.gui.button.GoBackButton;
import me.wega.cars.toolkit.gui.button.NextPageButton;
import me.wega.cars.toolkit.gui.button.PrevPageButton;
import me.wega.cars.toolkit.task.Tasks;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A pane that contains navigation buttons (go back, next page, previous page) for a paginated pane.
 */
public class PageNavPane extends StaticPane {
    private final Gui gui;
    private final PaginatedPane itemsPane;
    private final GuiItem goBackItem;
    private final Consumer<InventoryClickEvent> goBackAction;
    private final GuiItem nextPageItem = new NextPageButton(this::nextPageAction);

    /**
     * @implNote The paginatedPane needs to be filled with items before creating this pane.
     */
    public PageNavPane(@NotNull Gui gui, @NotNull PaginatedPane paginatedPane, int x, int y, int length, int height) {
        this(gui, paginatedPane, x, y, length, height, null);
    }
    public PageNavPane(@NotNull Gui gui, @NotNull PaginatedPane paginatedPane, int x, int y, int length, int height, @Nullable Consumer<InventoryClickEvent> goBackAction) {
        super(x, y, length, height);
        this.gui = gui;
        this.itemsPane = paginatedPane;
        this.goBackAction = goBackAction;

        this.goBackItem = (goBackAction != null)
                ? new GoBackButton(this::goBackAction)
                : new CloseButton();

        this.initialize();
    }    private final GuiItem previousPageItem = new PrevPageButton(this::previousPageAction);

    private void initialize() {
        this.addItem(previousPageItem, 3, 0);
        this.addItem(goBackItem, 4, 0);
        this.addItem(nextPageItem, 5, 0);
        this.updateNavigation();
    }

    private void nextPageAction(InventoryClickEvent event) {
        event.setCancelled(true);
        itemsPane.setPage(itemsPane.getPage() + 1);
        this.updateNavigation();
        gui.update();
    }

    private void previousPageAction(InventoryClickEvent event) {
        event.setCancelled(true);
        itemsPane.setPage(itemsPane.getPage() - 1);
        this.updateNavigation();
        gui.update();
    }

    private void goBackAction(InventoryClickEvent event) {
        event.setCancelled(true);
        WegaCars.INSTANCE.getTaskScheduler().schedule(Tasks.sync(WegaCars.INSTANCE, ctx -> {
            if (goBackAction != null)
                goBackAction.accept(event);
            else
                event.getWhoClicked().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        }));
    }

    private void updateNavigation() {
        int page = itemsPane.getPage();
        int totalPages = itemsPane.getPages();
        if (page == 0)
            previousPageItem.setVisible(false);
        else {
            previousPageItem.getItem().setAmount(page);
            previousPageItem.setVisible(true);
        }
        if (page == totalPages - 1)
            nextPageItem.setVisible(false);
        else {
            nextPageItem.setVisible(true);
            nextPageItem.getItem().setAmount(page + 2);
        }
    }




}
