package me.wega.cars.toolkit.gui.pane;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.util.Mask;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PaginatedMaskPane extends PaginatedPane {
    public PaginatedMaskPane(@NotNull Slot slot, int length, int height, @NotNull Priority priority) {
        super(slot, length, height, priority);
    }

    public PaginatedMaskPane(int x, int y, int length, int height, @NotNull Priority priority) {
        super(x, y, length, height, priority);
    }

    public PaginatedMaskPane(int x, int y, int length, int height) {
        super(x, y, length, height);
    }

    public PaginatedMaskPane(int length, int height) {
        super(length, height);
    }

    public PaginatedMaskPane(@NotNull Slot slot, int length, int height) {
        super(slot, length, height);
    }

    /**
     * Populates the PaginatedPane based on the provided list by adding new pages until all items can fit.
     * This can be helpful when dealing with lists of unknown size.
     * Also takes a mask.
     *
     * @param items The list to populate the pane with
     * @since 0.10.8
     */
    public void populateWithItemStacks(@NotNull List<@NotNull ItemStack> items, @NotNull Mask mask) {
        //Don't do anything if the list is empty
        if (items.isEmpty()) return;

        final int itemsPerPage = this.getEnabledSlotCount(mask);
        final int pagesNeeded = (int) Math.max(Math.ceil(items.size() / (double) itemsPerPage), 1);

        for (int i = 0; i < pagesNeeded; i++) {
            final OutlinePane page = new OutlinePane(0, 0, this.length, this.height);
            page.applyMask(mask);

            for (int j = 0; j < itemsPerPage; j++) {
                //Check if the loop reached the end of the list
                final int index = i * itemsPerPage + j;

                if (index >= items.size()) break;

                page.addItem(new GuiItem(items.get(index)));
            }

            this.addPane(i, page);
        }
    }

    /**
     * Populates the PaginatedPane based on the provided list by adding new pages until all items can fit.
     * This can be helpful when dealing with lists of unknown size.
     * Also takes a mask.
     *
     * @param items The list to populate the pane with
     * @since 0.10.8
     */
    public void populateWithGuiItems(@NotNull List<@NotNull GuiItem> items, @NotNull Mask mask) {
        //Don't do anything if the list is empty
        if (items.isEmpty()) return;

        final int itemsPerPage = this.getEnabledSlotCount(mask);
        final int pagesNeeded = (int) Math.max(Math.ceil(items.size() / (double) itemsPerPage), 1);

        for (int i = 0; i < pagesNeeded; i++) {
            final OutlinePane page = new OutlinePane(0, 0, this.length, this.height);
            page.applyMask(mask);

            for (int j = 0; j < itemsPerPage; j++) {
                final int index = i * itemsPerPage + j;

                //Check if the loop reached the end of the list
                if (index >= items.size()) break;

                page.addItem(items.get(index));
            }

            this.addPane(i, page);
        }
    }


    /**
     * Counts the number of enabled slots in the given mask.
     *
     * @param mask The mask to count enabled slots in
     * @return The number of enabled slots
     */
    private int getEnabledSlotCount(Mask mask) {
        int count = 0;
        for (int y = 0; y < mask.getHeight(); y++) {
            for (int x = 0; x < mask.getLength(); x++) {
                if (mask.isEnabled(x, y)) {
                    count++;
                }
            }
        }
        return count;
    }
}