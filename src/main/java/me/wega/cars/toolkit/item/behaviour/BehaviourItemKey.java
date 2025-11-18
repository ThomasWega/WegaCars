package me.wega.cars.toolkit.item.behaviour;

import me.wega.cars.WegaCars;
import me.wega.cars.toolkit.pdc.key.IPDCKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@RequiredArgsConstructor
public enum BehaviourItemKey implements IPDCKey {
    ID("item-behaviour-id", PersistentDataType.STRING);

    private final String key;
    private final PersistentDataType<?, ?> type;
    private final JavaPlugin instance = WegaCars.INSTANCE;
}
