package me.wega.cars.listener;

import me.wega.cars.annotation.BukkitListener;
import me.wega.cars.WegaCars;
import me.wega.cars.vehicle.Vehicle;
import com.ticxo.modelengine.api.events.ModelDismountEvent;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@BukkitListener
public class VehicleUnmountListener implements Listener {

    @EventHandler
    public void onInteract(ModelDismountEvent event) {
        if (!(event.getVehicle().getModeledEntity().getBase().getOriginal() instanceof Cow cow))
            return;

        final Vehicle vehicle = Vehicle.fromEntity(cow);
        if (vehicle == null) return;

        if (!(event.getPassenger() instanceof Player player)) return;

        vehicle.dismount();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Vehicle vehicle = WegaCars.INSTANCE.getVehiclePlayerMapManager().get(event.getPlayer());
        if (vehicle == null) return;

        vehicle.dismount();
    }
}
