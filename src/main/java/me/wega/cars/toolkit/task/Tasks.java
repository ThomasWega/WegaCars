package me.wega.cars.toolkit.task;

import lombok.experimental.UtilityClass;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Utility class providing factory methods for common task patterns.
 * All methods create pre-configured tasks for specific use cases.
 */
@UtilityClass
public class Tasks {

    /**
     * Creates a task that executes on the main thread.
     *
     * @param plugin The plugin instance
     * @param action The code to execute
     * @return A configured task
     */
    public static @NotNull Task sync(@NotNull JavaPlugin plugin, @NotNull Consumer<@NotNull TaskContext> action) {
        return new Task() {
            @Override
            public void execute(final @NotNull TaskContext context) {
                action.accept(context);
            }

            @Override
            public @NotNull JavaPlugin getPlugin() {
                return plugin;
            }

            @Override
            public @NotNull TaskConfiguration getConfiguration() {
                return TaskConfiguration.builder()
                        .async(false)
                        .build();
            }
        };
    }

    /**
     * Creates a custom task with a specific configuration.
     *
     * @param plugin        The plugin instance
     * @param configuration The task configuration
     * @param action        The code to execute
     * @return A configured task
     */
    public static @NotNull Task custom(@NotNull JavaPlugin plugin, @NotNull TaskConfiguration configuration, @NotNull Consumer<@NotNull TaskContext> action) {
        return new Task() {
            @Override
            public void execute(final @NotNull TaskContext context) {
                action.accept(context);
            }

            @Override
            public @NotNull JavaPlugin getPlugin() {
                return plugin;
            }

            @Override
            public @NotNull TaskConfiguration getConfiguration() {
                return configuration;
            }
        };
    }

    /**
     * Creates a task that executes on an async thread.
     *
     * @param plugin The plugin instance
     * @param action The code to execute
     * @return A configured task
     */
    public static @NotNull Task async(@NotNull JavaPlugin plugin, @NotNull Consumer<@NotNull TaskContext> action) {
        return new Task() {
            @Override
            public void execute(final @NotNull TaskContext context) {
                action.accept(context);
            }

            @Override
            public @NotNull JavaPlugin getPlugin() {
                return plugin;
            }

            @Override
            public @NotNull TaskConfiguration getConfiguration() {
                return TaskConfiguration.builder()
                        .async(true)
                        .build();
            }
        };
    }

    /**
     * Creates a task that executes once after a delay on the main thread.
     *
     * @param plugin The plugin instance
     * @param delay  The delay in ticks before execution
     * @param action The code to execute
     * @return A configured task
     */
    public static @NotNull Task later(@NotNull JavaPlugin plugin, long delay, @NotNull Consumer<@NotNull TaskContext> action) {
        return new Task() {
            @Override
            public void execute(final @NotNull TaskContext context) {
                action.accept(context);
            }

            @Override
            public @NotNull JavaPlugin getPlugin() {
                return plugin;
            }

            @Override
            public @NotNull TaskConfiguration getConfiguration() {
                return TaskConfiguration.builder()
                        .async(false)
                        .initialDelay(delay)
                        .build();
            }
        };
    }

    /**
     * Creates a task that executes once after a delay asynchronously.
     *
     * @param plugin The plugin instance
     * @param delay  The delay in ticks before execution
     * @param action The code to execute
     * @return A configured task
     */
    public static @NotNull Task laterAsync(@NotNull JavaPlugin plugin, long delay, @NotNull Consumer<@NotNull TaskContext> action) {
        return new Task() {
            @Override
            public void execute(final @NotNull TaskContext context) {
                action.accept(context);
            }

            @Override
            public @NotNull JavaPlugin getPlugin() {
                return plugin;
            }

            @Override
            public @NotNull TaskConfiguration getConfiguration() {
                return TaskConfiguration.builder()
                        .async(true)
                        .initialDelay(delay)
                        .build();
            }
        };
    }

    /**
     * Creates a repeating task that runs on the main thread.
     *
     * @param plugin   The plugin instance
     * @param delay    The initial delay in ticks
     * @param interval The interval between executions in ticks
     * @param action   The code to execute
     * @return A configured task
     */
    public static @NotNull Task timer(@NotNull JavaPlugin plugin, long delay, long interval, @NotNull Consumer<@NotNull TaskContext> action) {
        return new Task() {
            @Override
            public void execute(final @NotNull TaskContext context) {
                action.accept(context);
            }

            @Override
            public @NotNull JavaPlugin getPlugin() {
                return plugin;
            }

            @Override
            public @NotNull TaskConfiguration getConfiguration() {
                return TaskConfiguration.builder()
                        .async(false)
                        .initialDelay(delay)
                        .interval(interval)
                        .build();
            }
        };
    }

    /**
     * Creates a repeating task that runs asynchronously.
     *
     * @param plugin   The plugin instance
     * @param delay    The initial delay in ticks
     * @param interval The interval between executions in ticks
     * @param action   The code to execute
     * @return A configured task
     */
    public static @NotNull Task timerAsync(@NotNull JavaPlugin plugin, long delay, long interval, @NotNull Consumer<@NotNull TaskContext> action) {
        return new Task() {
            @Override
            public void execute(final @NotNull TaskContext context) {
                action.accept(context);
            }

            @Override
            public @NotNull JavaPlugin getPlugin() {
                return plugin;
            }

            @Override
            public @NotNull TaskConfiguration getConfiguration() {
                return TaskConfiguration.builder()
                        .async(true)
                        .initialDelay(delay)
                        .interval(interval)
                        .build();
            }
        };
    }

    /**
     * Creates a repeating task that runs for a specific duration on the main thread.
     *
     * @param plugin   The plugin instance
     * @param delay    The initial delay in ticks
     * @param interval The interval between executions in ticks
     * @param duration The total duration to run for in ticks
     * @param action   The code to execute
     * @return A configured task
     */
    public static @NotNull Task timerFor(@NotNull JavaPlugin plugin, long delay, long interval, long duration, @NotNull Consumer<@NotNull TaskContext> action) {
        return new Task() {
            @Override
            public void execute(final @NotNull TaskContext context) {
                action.accept(context);
            }

            @Override
            public @NotNull JavaPlugin getPlugin() {
                return plugin;
            }

            @Override
            public @NotNull TaskConfiguration getConfiguration() {
                return TaskConfiguration.builder()
                        .async(false)
                        .initialDelay(delay)
                        .interval(interval)
                        .duration(duration)
                        .build();
            }
        };
    }

    /**
     * Creates a repeating task that runs a specific number of times on the main thread.
     *
     * @param plugin   The plugin instance
     * @param delay    The initial delay in ticks
     * @param interval The interval between executions in ticks
     * @param times    The number of times to execute
     * @param action   The code to execute
     * @return A configured task
     */
    public static @NotNull Task timerTimes(@NotNull JavaPlugin plugin, long delay, long interval, long times, @NotNull Consumer<@NotNull TaskContext> action) {
        return new Task() {
            @Override
            public void execute(final @NotNull TaskContext context) {
                action.accept(context);
            }

            @Override
            public @NotNull JavaPlugin getPlugin() {
                return plugin;
            }

            @Override
            public @NotNull TaskConfiguration getConfiguration() {
                return TaskConfiguration.builder()
                        .async(false)
                        .initialDelay(delay)
                        .interval(interval)
                        .repetitions(times)
                        .build();
            }
        };
    }
}
