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
import java.util.List;
import java.util.logging.Level;

/**
 * A data manager with saving capabilities.
 * This class provides methods to save and load a list to a file.
 *
 * @param <V> The value type of the list.
 */
@RequiredArgsConstructor
@Getter
public abstract class SavingDataManager<V> extends DataManager<V> {
    private final @NotNull JavaPlugin plugin;
    private final @NotNull DataSavingManager dataSavingManager;
    private final @NotNull Gson gson;
    private final @NotNull File file;
    private final @NotNull Type listType;

    {
        this.scheduleSaving(this::queueSave);
    }

    public void queueSave() {
        dataSavingManager.queueSave(file.getPath() + " save", this::saveAction);
    }

    /**
     * Schedules the saving of the list.
     * Can look something like this:
     * <pre>{@code
     *         taskScheduler.schedule(Tasks.timerAsync(RPCore.INSTANCE, 20 * 300, 20 * 300, saveAction));
     *         }</pre>
     * <p>
     * It's important that the tasks in scheduled using the plugin's task scheduler.
     *
     * @param saveAction The action to save the list
     */
    public void scheduleSaving(final @NotNull Runnable saveAction) {

    }

    /**
     * Saves the list to the file.
     * If the file does not exist, this method should create the parent directories.
     *
     * @return true if the list was saved successfully.
     * @implNote Can be overridden to provide custom saving behavior.
     */
    public boolean saveAction() {
        if (!file.exists())
            file.getParentFile().mkdirs();

        try (final FileWriter writer = new FileWriter(file)) {
            gson.toJson(this.getSaveObject(), listType, writer);
            return true;
        } catch (final IOException err) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save " + file.getPath(), err);
            return false;
        }
    }

    /**
     * Loads the list from the file.
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
            final Object list = gson.fromJson(reader, listType);
            if (list != null)
                this.loadSetAction(list);
            return true;
        } catch (final IOException err) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load " + file.getPath(), err);
            return false;
        }
    }

    /**
     * @return The object to save in {@link #saveAction()}.
     * Should be the same as the list type provided in the constructor.
     * @implNote By default, this method returns the list.
     */
    public @NotNull Object getSaveObject() {
        return this.getSet();
    }

    /**
     * Sets the list from the object loaded from the file.
     * Should be the same as the list type provided in the constructor.
     *
     * @param object The object to load the map from.
     * @implNote By default, this method casts the object to a list and lists it.
     */
    public void loadSetAction(@NotNull Object object) {
        this.set((List<V>) object);
    }
}
