package me.wega.cars.item.lift.tasks;

import me.wega.cars.WegaCars;
import me.wega.cars.VehiclesConfig;
import me.wega.cars.item.lift.VehicleLift;
import me.wega.cars.toolkit.date.CustomTimeUnit;
import me.wega.cars.toolkit.task.AbstractTask;
import me.wega.cars.toolkit.task.TaskConfiguration;
import me.wega.cars.toolkit.task.TaskContext;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

public class VehicleLiftTask extends AbstractTask {
    public VehicleLiftTask() {
        super(WegaCars.INSTANCE);
    }

    @Override
    protected @NotNull TaskConfiguration createConfiguration() {
        return TaskConfiguration.builder()
                .interval(1L)
                .timeUnit(CustomTimeUnit.SECONDS)
                .build();
    }

    @Override
    public void execute(@NotNull TaskContext taskContext) {
        for (VehicleLift vehicleLift : WegaCars.INSTANCE.getVehicleLiftManager().getSet()) {
            if (vehicleLift.hasVehicle()) continue;

            final Location liftLocation = vehicleLift.getLiftLocation();
            final int maxDistance = VehiclesConfig.MAX_LIFT_DISTANCE.get();
            for (double x = liftLocation.getX() - maxDistance; x <= liftLocation.getX() + maxDistance; x++) {
                for (double z = liftLocation.getZ() - maxDistance; z <= liftLocation.getZ() + maxDistance; z++) {
                    final Location particle = new Location(liftLocation.getWorld(), x, liftLocation.getY(), z);
                    particle.getWorld().spawnParticle(Particle.FLAME, particle, 0);
                }
            }
        }
    }
}
