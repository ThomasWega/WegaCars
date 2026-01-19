package me.wega.cars.listener;

import me.wega.cars.annotation.BukkitListener;
import me.wega.cars.utils.VehicleEntityLoadHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.EntitiesLoadEvent;

@BukkitListener
public class VehicleEntityLoadListener implements Listener {

    @EventHandler
    public void onEntityLoad(EntitiesLoadEvent event) {
        event.getEntities().forEach(VehicleEntityLoadHandler::handleEntityLoad);
    }
}
