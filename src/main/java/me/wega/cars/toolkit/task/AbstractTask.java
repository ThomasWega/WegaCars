package me.wega.cars.toolkit.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public abstract class AbstractTask implements Task {
    @Getter
    private final @NotNull JavaPlugin plugin;

    @Getter(lazy = true)
    private final @NotNull TaskConfiguration configuration = this.createConfiguration();

    /**
     * Creates the configuration for this task. This method is expected to be implemented
     * by subclasses to define task-specific configurations such as execution intervals,
     * delays, and repetition constraints.
     *
     * @return a {@link TaskConfiguration} object with settings for this task
     */
    protected abstract @NotNull TaskConfiguration createConfiguration();

    @Override
    public void onStart(final @NotNull TaskContext context) {
        // Default empty implementation that can be overridden by subclasses
    }
}