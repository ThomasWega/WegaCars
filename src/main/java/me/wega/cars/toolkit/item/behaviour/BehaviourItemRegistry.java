package me.wega.cars.toolkit.item.behaviour;

import me.wega.cars.WegaCars;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BehaviourItemRegistry {
    public static final BehaviourItemRegistry DEFAULT = new BehaviourItemRegistry();
    private final @NotNull Map<String, BehaviourItem> behaviourItemMap = new HashMap<>();

    public void registerItem(@NotNull BehaviourItem customItem) {
        if (this.behaviourItemMap.containsKey(customItem.getId()))
            WegaCars.INSTANCE.getLogger().warning(customItem.getId() + " was already registered.");

        customItem.init();
        this.behaviourItemMap.put(customItem.getId(), customItem);
    }

    public @Nullable BehaviourItem getItem(@NotNull UUID uniqueId) {
        return this.behaviourItemMap.get(uniqueId);
    }
}
