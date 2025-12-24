package me.wega.cars.toolkit.data;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.wega.cars.toolkit.file.FileLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.logging.Level;

/**
 * A data map manager with saving capabilities.
 * This class provides methods to save and load a map to a file.
 *
 * @param <K> The key type of the map.
 * @param <V> The value type of the map.
 */
@RequiredArgsConstructor
@Getter
public abstract class SavingDataMapManager<K, V> extends DataMapManager<K, V> {
    private final @NotNull JavaPlugin plugin;
    private final @NotNull DataSavingManager dataSavingManager;
    private final @NotNull Gson gson;
    private final @NotNull File file;
    private final @NotNull Type mapType;

    {
        this.scheduleSaving(this::queueSave);
    }

    public SavingDataMapManager(@NotNull Map<K, V> mapType,
                                @NotNull JavaPlugin plugin,
                                @NotNull DataSavingManager dataSavingManager,
                                @NotNull Gson gson,
                                @NotNull File file,
                                @NotNull Type type) {
        super(mapType);
        this.plugin = plugin;
        this.dataSavingManager = dataSavingManager;
        this.gson = gson;
        this.file = file;
        this.mapType = type;
    }

    public void queueSave() {
        dataSavingManager.queueSave(file.getPath() + " save", this::saveAction);
    }

    /**
     * Schedules the saving of the map.
     * Can look something like this:
     * <pre>{@code
     *         taskScheduler.schedule(Tasks.timerAsync(WegaCars.INSTANCE, 20 * 300, 20 * 300, saveAction));
     *         }</pre>
     * <p>
     * It's important that the tasks in scheduled using the plugin's task scheduler.
     *
     * @param saveAction The action to save the map.
     */
    public void scheduleSaving(final @NotNull Runnable saveAction) {

    }

    /**
     * Saves the map to the file.
     * If the file does not exist, this method should create the parent directories.
     *
     * @return true if the map was saved successfully.
     * @implNote Can be overridden to provide custom saving behavior.
     */
    public boolean saveAction() {
        if (!file.exists())
            file.getParentFile().mkdirs();

        try (final FileWriter writer = new FileWriter(file)) {
            gson.toJson(this.getSaveObject(), mapType, writer);
            return true;
        } catch (final IOException err) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save " + file.getPath(), err);
            return false;
        }
    }

    /**
     * Loads the map from the file.
     * If the file does not exist, this method should return true.
     *
     * @param createDefault Whether to create a default file from resources if the file does not exist.
     * @return true if the file does not exist or the map was loaded successfully.
     * @implNote Can be overridden to provide custom loading behavior.
     */
    public boolean loadAction(boolean createDefault) {
        if (!file.exists()) {
            if (createDefault)
                FileLoader.loadSingleFile(plugin, file);
            else return true;
        }

        try (final FileReader reader = new FileReader(file)) {
            final Object map = gson.fromJson(reader, mapType);
            if (map != null)
                this.loadSetAction(map);
            return true;
        } catch (final IOException err) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load " + file.getPath(), err);
            return false;
        }
    }

    /**
     * @return The object to save in {@link #saveAction()}.
     * Should be the same as the map type provided in the constructor.
     * @implNote By default, this method returns the map.
     */
    public @NotNull Object getSaveObject() {
        return this.getMap();
    }

    /**
     * Sets the map from the object loaded from the file.
     * Should be the same as the map type provided in the constructor.
     *
     * @param object The object to load the map from.
     * @implNote By default, this method casts the object to a map and sets it.
     */
    public void loadSetAction(@NotNull Object object) {
        this.set((Map<K, V>) object);
    }
}
