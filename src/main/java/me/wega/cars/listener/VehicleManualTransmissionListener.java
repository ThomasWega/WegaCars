package me.wega.cars.listener;

import me.wega.cars.annotation.BukkitListener;
import me.wega.cars.WegaCars;
import me.wega.cars.vehicle.Vehicle;
import me.wega.cars.vehicle.transmission.tasks.ManualTransmissionMinigame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.jetbrains.annotations.Nullable;

@BukkitListener
public class VehicleManualTransmissionListener implements Listener {

    @EventHandler
    public void onChange(PlayerItemHeldEvent event) {
        final @Nullable Vehicle vehicle = WegaCars.INSTANCE.getVehiclePlayerMapManager().get(event.getPlayer());
        if (vehicle == null || !vehicle.hasController()) return;

        final ManualTransmissionMinigame minigame = vehicle.getController().getManualTransmissionMinigame();
        if (minigame == null) return;

        minigame.checkSlot(event.getNewSlot());
    }
}
