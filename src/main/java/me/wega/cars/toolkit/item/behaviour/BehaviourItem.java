package me.wega.cars.toolkit.item.behaviour;

import me.wega.cars.toolkit.item.behaviour.impl.CancelMoveBehaviour;
import me.wega.cars.toolkit.item.behaviour.impl.ItemBlockBreakBehaviour;
import me.wega.cars.toolkit.item.behaviour.impl.ItemBlockPlaceBehaviour;
import me.wega.cars.toolkit.item.behaviour.impl.ItemInteractBehaviour;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class BehaviourItem {
    @Getter
    private final @NotNull String id;
    private final @NotNull ItemStack itemStack;

    private final @NotNull List<ItemBehaviour> behaviours;
    private boolean initialized;

    public static Builder builder(@NotNull String id, @NotNull ItemStack itemStack) {
        return new Builder(id, itemStack);
    }

    public @NotNull ItemStack getItemStack() {
        return itemStack.clone();
    }

    public @NotNull ItemStack getItemStack(int amount) {
        final ItemStack clone = itemStack.clone();
        clone.setAmount(amount);
        return clone;
    }

    public void init() {
        if (this.initialized) return;

        this.initialized = true;
        for (ItemBehaviour behaviour : this.behaviours) {
            behaviour.setBehaviourItem(this);
            behaviour.init();
        }
    }

    public static class Builder {
        private final @NotNull String id;
        private final @NotNull ItemStack itemStack;
        private final @NotNull List<ItemBehaviour> behaviours = new ArrayList<>();

        private Builder(@NotNull String id, @NotNull ItemStack itemStack) {
            this.id = id;
            final ItemStack clone = itemStack.clone();

            final ItemMeta itemMeta = clone.getItemMeta();
            BehaviourItemKey.ID.set(itemMeta, this.id);
            clone.setItemMeta(itemMeta);

            this.itemStack = clone;
        }

        public @NotNull Builder behaviour(@NotNull ItemBehaviour behaviour) {
            this.behaviours.add(behaviour);
            return this;
        }

        public @NotNull Builder onBreak(@NotNull Consumer<BlockBreakEvent> eventConsumer) {
            this.behaviours.add(new ItemBlockBreakBehaviour(eventConsumer));
            return this;
        }

        public @NotNull Builder onPlace(@NotNull Consumer<BlockPlaceEvent> eventConsumer) {
            this.behaviours.add(new ItemBlockPlaceBehaviour(eventConsumer));
            return this;
        }

        public @NotNull Builder onInteract(@NotNull Consumer<PlayerInteractEvent> eventConsumer) {
            this.behaviours.add(new ItemInteractBehaviour(eventConsumer));
            return this;
        }

        public @NotNull Builder cancelMove(@Nullable Consumer<@NotNull Event> eventConsumer) {
            this.behaviours.add(new CancelMoveBehaviour(eventConsumer));
            return this;
        }

        public <T extends Event> @NotNull Builder behaviourOfEvent(@NotNull Class<T> eventClass, @NotNull Consumer<@NotNull T> listener) {
            this.behaviours.add(new CustomItemEventBehaviour<T>(eventClass) {
                @Override
                public void onEvent(T event) {
                    listener.accept(event);
                }
            });
            return this;
        }

        public @NotNull BehaviourItem create(@NotNull BehaviourItemRegistry registry) {
            final BehaviourItem customItem = new BehaviourItem(this.id, this.itemStack, this.behaviours);
            registry.registerItem(customItem);

            return customItem;
        }
    }
}
