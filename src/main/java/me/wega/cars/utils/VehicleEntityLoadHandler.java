package me.wega.cars.utils;

import me.wega.cars.WegaCars;
import me.wega.cars.vehicle.Vehicle;
import me.wega.cars.toolkit.task.Tasks;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;

public class VehicleEntityLoadHandler {

    public static void handleEntityLoad(Entity entity) {
        if (!(entity instanceof Cow))
            return;

        final Vehicle vehicle = Vehicle.fromEntity(entity);
        if (vehicle == null) return;

        WegaCars.INSTANCE.getTaskScheduler().schedule(Tasks.sync(WegaCars.INSTANCE, ctx -> vehicle.createBaseModel()));
    }
}
