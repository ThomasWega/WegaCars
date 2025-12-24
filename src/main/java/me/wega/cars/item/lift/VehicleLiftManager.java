package me.wega.cars.item.lift;

import com.google.gson.reflect.TypeToken;
import me.wega.cars.WegaCars;
import me.wega.cars.toolkit.data.SavingDataManager;

import java.io.File;
import java.util.List;

public class VehicleLiftManager extends SavingDataManager<VehicleLift> {

    public VehicleLiftManager() {
        super(
                WegaCars.INSTANCE,
                WegaCars.INSTANCE.getDataSavingManager(),
                WegaCars.INSTANCE.getGson(),
                new File(WegaCars.INSTANCE.getDataDir(), "vehicle_lifts.json"),
                new TypeToken<List<VehicleLift>>() {}.getType()
        );
    }
}