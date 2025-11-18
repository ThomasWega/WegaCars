package me.wega.cars.toolkit.config;

import me.wega.cars.toolkit.config.property.ConfigProperty;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class ConfigPropertyClass {
    protected final @NotNull JavaPlugin plugin;
    protected @NotNull YamlConfiguration config;
    protected final File file;
    protected final @NotNull List<@NotNull Runnable> reloadCallbacks = new ArrayList<>();

    public ConfigPropertyClass(@NotNull JavaPlugin plugin, @NotNull File file) {
        this.plugin = plugin;
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
        this.registerConfigProperties();
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
        reloadCallbacks.forEach(Runnable::run);
    }

    public void onReload(@NotNull Runnable callback) {
        reloadCallbacks.add(callback);
    }

    public void removeReloadCallback(@NotNull Runnable callback) {
        reloadCallbacks.remove(callback);
    }

    public void clearReloadCallbacks() {
        reloadCallbacks.clear();
    }

    private void registerConfigProperties() {
        // Process current class fields
        this.processFieldsForClass(this.getClass(), this);

        // Process nested classes
        for (Class<?> nestedClass : this.getClass().getDeclaredClasses())
            this.processFieldsForClass(nestedClass, this);
    }

    private void processFieldsForClass(Class<?> clazz, Object instance) {
        for (Field field : clazz.getDeclaredFields()) {
            if (ConfigProperty.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                try {
                    final ConfigProperty<?> property = (ConfigProperty<?>) field.get(instance);
                    if (property != null) {
                        property.setPropertyClass(this);
                        this.onReload(property::onReload);
                        property.setDefault();
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to access ConfigProperty field: " + field.getName(), e);
                }
            }
        }

        // Recursively process all nested classes
        for (Class<?> nestedClass : clazz.getDeclaredClasses()) {
            this.processFieldsForClass(nestedClass, instance);
        }
    }
}