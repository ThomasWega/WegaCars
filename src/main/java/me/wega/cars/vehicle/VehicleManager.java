package me.wega.cars.vehicle;

import me.wega.cars.toolkit.data.SavingDataMapManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

import static me.wega.cars.WegaCars.INSTANCE;

public class VehicleManager extends SavingDataMapManager<UUID, Vehicle> {

    public VehicleManager() {
        super(
                INSTANCE,
                INSTANCE.getDataSavingManager(),
                INSTANCE.getGson(),
                new File(INSTANCE.getDataDir(), "vehicle_data.json"),
                Vehicle[].class
        );
    }

    @Override
    public @NotNull Object getSaveObject() {
        return this.getValues().toArray(Vehicle[]::new);
    }

    @Override
    public void loadSetAction(@NotNull Object object) {
        final Vehicle[] vehicles = (Vehicle[]) object;
        Arrays.stream(vehicles).forEach(vehicle -> this.add(vehicle.getUniqueId(), vehicle));
    }
}