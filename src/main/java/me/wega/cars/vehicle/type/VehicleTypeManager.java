package me.wega.cars.vehicle.type;

import me.wega.cars.toolkit.data.SavingDataMapManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;

import static me.wega.cars.WegaCars.INSTANCE;

public class VehicleTypeManager extends SavingDataMapManager<String, VehicleType> {

    public VehicleTypeManager() {
        super(
                INSTANCE,
                INSTANCE.getDataSavingManager(),
                INSTANCE.getGson(),
                new File(INSTANCE.getDataFolder(), "vehicle_types.json"),
                VehicleType[].class
        );
    }

    @Override
    public @NotNull Object getSaveObject() {
        return this.getValues().toArray(VehicleType[]::new);
    }

    @Override
    public void loadSetAction(@NotNull Object object) {
        final VehicleType[] vehicleTypes = (VehicleType[]) object;
        Arrays.stream(vehicleTypes).forEach(type -> this.add(type.getId(), type));
    }
}