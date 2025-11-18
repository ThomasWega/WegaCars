package me.wega.cars.toolkit.gui.impl;

import com.github.stefvanschie.inventoryframework.adventuresupport.TextHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.wega.cars.toolkit.builder.ItemBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TextInputGUI extends AnvilGui {
    protected final StaticPane inputPane = new StaticPane(0, 0, 1, 1);
    protected final StaticPane outputPane = new StaticPane(0, 0, 1, 1);

    @Getter
    protected final @NotNull Player player;

    private long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL_MS = 250; // ms // a low ms value causes issues
    private final @Nullable ItemStack inputItem;
    private @Nullable ItemStack outputItem;

    protected TextInputGUI(@NotNull TextHolder title, @NotNull Player player, @Nullable ItemStack inputItem, @Nullable ItemStack outputItem) {
        super(title);
        this.player = player;
        this.inputItem = inputItem;
        this.outputItem = outputItem;
        this.initialize();
    }

    private void initialize() {
        this.getFirstItemComponent().addPane(this.inputPane);
        this.getResultComponent().addPane(this.outputPane);

        this.inputPane.addItem(this.getInputButton(), 0, 0);
        this.setOnNameInputChanged(this::handleTextChange);
    }

    private GuiItem getInputButton() {
        if (inputItem == null) {
            return new GuiItem(new ItemBuilder(Material.NAME_TAG)
                    .displayName(Component.empty())
                    .build(), event -> event.setCancelled(true)
            );
        } else return new GuiItem(inputItem, event -> event.setCancelled(true));
    }

    private GuiItem getOutputButton(Component text) {
        if (outputItem == null) {
            final ItemStack newOutputItem = new ItemBuilder(Material.NAME_TAG)
                    .displayName(text)
                    .build();
            this.outputItem = newOutputItem;
            return new GuiItem(newOutputItem, event -> event.setCancelled(true)
            );
        } else return new GuiItem(
                new ItemBuilder(outputItem)
                        .displayName(text)
                        .build(),
                this::handleConfirm
        );
    }

    private void handleTextChange(@NotNull String currentText) {
        if (this.onCooldown()) return;

        final String finalText = currentText.trim();

        final Component processedComponent = this.handleTextInputComponent(finalText);

        this.setInputText(processedComponent);

        this.outputPane.addItem(this.getOutputButton(processedComponent), 0, 0);

        this.update();
    }

    private boolean onCooldown() {
        final long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime < UPDATE_INTERVAL_MS)
            return true;
        lastUpdateTime = currentTime;
        return false;
    }

    private void setInputText(@NotNull Component text) {
        this.inputPane.getItems().forEach(item ->
                item.getItem().editMeta(meta -> meta.displayName(text))
        );
    }

    private void handleConfirm(InventoryClickEvent event) {
        event.setCancelled(true);
        this.onConfirm(this.getRenameText());
    }

    /**
     * Process and validate the input text.
     *
     * @param input The current input text
     * @return The processed text to display or empty string to clear the text
     */
    protected @NotNull String handleTextInput(@NotNull String input) {
        return input;
    }


    /**
     * Process and validate the input text.
     *
     * @param input The current input text
     * @return The processed text to display or empty component to clear the text
     */
    protected @NotNull Component handleTextInputComponent(@NotNull String input) {
        return Component.text(this.handleTextInput(input));
    }

    /**
     * Handle the confirmation of the text input.
     *
     * @param finalText The final processed text
     */
    protected abstract void onConfirm(@NotNull String finalText);
}
