package me.wega.cars.toolkit.config.sound;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import me.wega.cars.toolkit.data.DataSavingManager;
import me.wega.cars.toolkit.data.SavingDataMapManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;

public class ConfigSounds extends SavingDataMapManager<String, ConfigSound> {

    public ConfigSounds(@NotNull JavaPlugin plugin,
                        @NotNull DataSavingManager dataSavingManager,
                        @NotNull Gson gson,
                        @NotNull File file) {
        super(
                plugin,
                dataSavingManager,
                gson,
                file,
                new TypeToken<Map<String, ConfigSound>>() {
                }.getType()
        );
    }

    @Override
    public @NotNull ConfigSound get(@NotNull String key) {
        return this.getOrThrow(key, () -> new NullPointerException("Sound with key " + key + " not found. Check your sounds file"));
    }
}
