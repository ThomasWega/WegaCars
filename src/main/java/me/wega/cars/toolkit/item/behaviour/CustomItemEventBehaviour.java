package me.wega.cars.toolkit.item.behaviour;

import me.wega.cars.WegaCars;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

public abstract class CustomItemEventBehaviour<T extends Event> extends ItemBehaviour {
    private final Class<T> eventClass;

    protected CustomItemEventBehaviour(@NotNull Class<T> event) {
        this.eventClass = event;
    }

    public abstract void onEvent(T event);

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvent(this.eventClass, this, EventPriority.NORMAL, (listener, event) ->
                onEvent((T) event), WegaCars.INSTANCE, false);
    }
}
