package me.wega.cars.toolkit.item.behaviour;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class ItemBehaviour implements Listener {

    private @Nullable BehaviourItem behaviourItem;

    public void init() {}

    public boolean matches(@NotNull ItemStack itemStack) {
        if (this.behaviourItem == null) return false;
        if (!itemStack.hasItemMeta()) return false;

        final ItemMeta itemMeta = itemStack.getItemMeta();
        return BehaviourItemKey.ID.has(itemMeta) && BehaviourItemKey.ID.getString(itemMeta).equals(this.behaviourItem.getId());
    }
}
