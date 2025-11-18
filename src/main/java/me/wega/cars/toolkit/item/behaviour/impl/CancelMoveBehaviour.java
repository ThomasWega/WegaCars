package me.wega.cars.toolkit.item.behaviour.impl;

import me.wega.cars.WegaCars;
import me.wega.cars.toolkit.item.behaviour.ItemBehaviour;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CancelMoveBehaviour extends ItemBehaviour {
    private final @Nullable Consumer<@NotNull Event> callback;

    public CancelMoveBehaviour(@Nullable Consumer<@NotNull Event> callback) {
        this.callback = callback;
    }

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvents(this, WegaCars.INSTANCE);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        ItemStack dropItem = event.getItemDrop().getItemStack();

        this.checkItem(event, dropItem);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) return;
        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        this.checkItem(event, currentItem);
        this.checkItem(event, cursorItem);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onDrag(InventoryDragEvent event) {
        if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) return;
        ItemStack cursorItem = event.getOldCursor();

        this.checkItem(event, cursorItem);
    }

    private <T extends Event & Cancellable> void checkItem(@NotNull T event, @Nullable ItemStack itemStack) {
        if (itemStack != null && !itemStack.getType().isEmpty()) {
            if (this.matches(itemStack)) {
                event.setCancelled(true);
                if (this.callback != null)
                    this.callback.accept(event);
            }
        }
    }
}
