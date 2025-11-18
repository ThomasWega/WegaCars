package me.wega.cars.toolkit.item.behaviour.impl;

import me.wega.cars.toolkit.item.behaviour.CustomItemEventBehaviour;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemBlockBreakBehaviour extends CustomItemEventBehaviour<BlockBreakEvent> {
    private final Consumer<BlockBreakEvent> eventConsumer;

    public ItemBlockBreakBehaviour(@NotNull Consumer<BlockBreakEvent> eventConsumer) {
        super(BlockBreakEvent.class);
        this.eventConsumer = eventConsumer;
    }

    @Override
    public void onEvent(BlockBreakEvent event) {
        if (!this.matches(event.getPlayer().getInventory().getItemInMainHand())) return;

        this.eventConsumer.accept(event);
    }
}
