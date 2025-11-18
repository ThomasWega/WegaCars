package me.wega.cars.toolkit.item.behaviour.impl;

import me.wega.cars.toolkit.item.behaviour.CustomItemEventBehaviour;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemInteractBehaviour extends CustomItemEventBehaviour<PlayerInteractEvent> {
    private final Consumer<PlayerInteractEvent> eventConsumer;

    public ItemInteractBehaviour(@NotNull Consumer<PlayerInteractEvent> eventConsumer) {
        super(PlayerInteractEvent.class);
        this.eventConsumer = eventConsumer;
    }

    @Override
    public void onEvent(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (!this.matches(event.getItem())) return;

        this.eventConsumer.accept(event);
    }
}
