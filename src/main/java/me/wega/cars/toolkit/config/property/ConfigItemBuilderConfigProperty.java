package me.wega.cars.toolkit.config.property;

import me.wega.cars.toolkit.builder.ConfigItemBuilder;
import org.jetbrains.annotations.NotNull;

public class ConfigItemBuilderConfigProperty extends ConfigProperty<ConfigItemBuilder> {

    public ConfigItemBuilderConfigProperty(@NotNull String path) {
        super(path, config2 -> new ConfigItemBuilder(config2.getConfigurationSection(path)));
    }
}
