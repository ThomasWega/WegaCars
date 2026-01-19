package me.wega.cars.vehicle.transmission.tasks;

import me.wega.cars.WegaCars;
import me.wega.cars.VehiclesConfig;
import me.wega.cars.vehicle.Vehicle;
import me.wega.cars.vehicle.VehicleStat;
import me.wega.cars.toolkit.date.CustomTimeUnit;
import me.wega.cars.toolkit.task.AbstractTask;
import me.wega.cars.toolkit.task.TaskConfiguration;
import me.wega.cars.toolkit.task.TaskContext;
import org.jetbrains.annotations.NotNull;

public class ManualTransmissionBoostTask extends AbstractTask {

    private final @NotNull Vehicle vehicle;
    private final int speed;
    public ManualTransmissionBoostTask(@NotNull Vehicle vehicle, int speed) {
        super(WegaCars.INSTANCE);
        this.vehicle = vehicle;
        this.speed = speed;
    }

    @Override
    protected @NotNull TaskConfiguration createConfiguration() {
        return TaskConfiguration.builder()
                .interval(1L)
                .timeUnit(CustomTimeUnit.TICKS)
                .repetitions(VehiclesConfig.MANUAL_TRANSMISSION_SPEED_BOOST_TIME.get().longValue())
                .continueCondition(this.vehicle::hasController)
                .build();
    }

    @Override
    public void execute(@NotNull TaskContext taskContext) {

    }

    @Override
    public void onStart(@NotNull TaskContext context) {
        this.vehicle.addStat(VehicleStat.TOP_SPEED, speed);
    }

    @Override
    public void onComplete(@NotNull TaskContext context, @NotNull CompletionReason reason) {
        this.vehicle.removeStat(VehicleStat.TOP_SPEED, speed);
    }
}
