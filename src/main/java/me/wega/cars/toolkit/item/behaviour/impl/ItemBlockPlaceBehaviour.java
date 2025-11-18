package me.wega.cars.toolkit.item.behaviour.impl;

import me.wega.cars.toolkit.item.behaviour.CustomItemEventBehaviour;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemBlockPlaceBehaviour extends CustomItemEventBehaviour<BlockPlaceEvent> {
    private final Consumer<BlockPlaceEvent> eventConsumer;

    public ItemBlockPlaceBehaviour(@NotNull Consumer<BlockPlaceEvent> eventConsumer) {
        super(BlockPlaceEvent.class);
        this.eventConsumer = eventConsumer;
    }

    @Override
    public void onEvent(BlockPlaceEvent event) {
        if (!this.matches(event.getItemInHand())) return;

        this.eventConsumer.accept(event);
    }
}
