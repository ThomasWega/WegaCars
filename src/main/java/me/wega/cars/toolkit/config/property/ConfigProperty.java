package me.wega.cars.toolkit.config.property;

import me.wega.cars.toolkit.config.ConfigPropertyClass;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Function;

/**
 * A simple class to ease the use of config values.
 * One instance of this class represents one value in the config.
 *
 * @param <T> The type of the property.
 */
public class ConfigProperty<T> {
    @Setter
    private ConfigPropertyClass propertyClass;
    private final @NotNull Function<@NotNull YamlConfiguration, T> valueSupplier;
    private final @NotNull String path;
    private T value;

    public ConfigProperty(@NotNull String path) {
        this(path, config2 -> (T) config2.get(path));
    }

    public ConfigProperty(@NotNull String path, @NotNull Function<@NotNull YamlConfiguration, T> valueSupplier) {
        this.valueSupplier = valueSupplier;
        this.path = path;
    }

    public void onReload() {
        value = null;
        this.setDefault();
        this.get();
    }

    /**
     * @return The value of the property.
     * @implNote Limitations:
     * <ul>
     *   <li>The value instance must be of the same type as the generic type.</li>
     *   <li>The value must be present in the config.</li>
     *   <li>The value must be non-null.</li>
     *   <li>Doesn't support Float, only Double.</li>
     *   <li>Doesn't support Long, only Integer.</li>
     * </ul>
     * @see FileConfiguration#get(String)
     */
    public T get() {
        if (value == null)
         value = valueSupplier.apply(propertyClass.getConfig());
        return value;
    }

    @SneakyThrows
    public void set(@Nullable Object value) {
        propertyClass.getConfig().set(path, value);
        propertyClass.getConfig().save(propertyClass.getFile());
        this.value = null;
    }

    @SneakyThrows
    public void setDefault() {
        final YamlConfiguration config = propertyClass.getConfig();
        if (!config.contains(path)) {
            final String relativePath = propertyClass.getPlugin().getDataFolder().toPath().relativize(propertyClass.getFile().toPath()).toString();
            final InputStream input = propertyClass.getPlugin().getResource(relativePath);
            if (input == null) return;

            final YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(input));
            if (defaultConfig.contains(path)) {
                Object defaultValue = defaultConfig.get(path);
                config.set(path, defaultValue);
                propertyClass.getPlugin().getLogger().warning("Auto-populated missing config value: " + path + " with default value: " + defaultValue);
                config.save(propertyClass.getFile());
            }
            input.close();
        }
    }
}
