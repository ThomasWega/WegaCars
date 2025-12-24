package me.wega.cars.item.part;

import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import me.wega.cars.WegaCars;
import me.wega.cars.toolkit.data.DataMapManager;
import me.wega.cars.toolkit.file.FileLoader;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.logging.Level;

public class VehiclePartManager extends DataMapManager<String, VehiclePart> {

    private final Type setType = new TypeToken<Set<VehiclePart>>() {
    }.getType();
    private final File itemDir = new File(WegaCars.INSTANCE.getDataFolder(), "items");

    public boolean loadAll() {
        for (VehiclePartType type : VehiclePartType.values()) {
            final File file = FileLoader.loadSingleFile(WegaCars.INSTANCE, new File(itemDir, type.getFileName()));
            if (!file.exists()) continue;
            try (JsonReader reader = new JsonReader(new FileReader(file))) {
                final Set<VehiclePart> items = WegaCars.INSTANCE.getGson().fromJson(reader, setType);
                if (items != null)
                    for (VehiclePart vehiclePart : items)
                        this.add(vehiclePart.getItemId(), vehiclePart);

            } catch (Exception e) {
                WegaCars.INSTANCE.getLogger().log(Level.SEVERE, "Failed to load items for " + type.name(), e);
                return false;
            }
        }
        return true;
    }
}