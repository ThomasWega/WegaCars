package me.wega.cars.toolkit.task;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public interface Task {
    void execute(final @NotNull TaskContext context);

    @NotNull JavaPlugin getPlugin();

    @NotNull TaskConfiguration getConfiguration();

    /**
     * Called once when the task is first scheduled, before any execution begins.
     * This method runs synchronously on the thread that schedules the task,
     * regardless of the task's async configuration.
     *
     * @param context The execution context containing runtime information about the task
     */
    default void onStart(final @NotNull TaskContext context) {
        // Default empty implementation
    }

    default void onComplete(final @NotNull TaskContext context, final @NotNull CompletionReason reason) {
        // Default empty implementation
    }

    enum CompletionReason {
        COMPLETED,
        CANCELLED,
        CONDITION_FALSE,
        DURATION_REACHED,
        REPETITIONS_REACHED;

        public boolean wasSuccessful() {
            return this == COMPLETED || this == DURATION_REACHED || this == REPETITIONS_REACHED;
        }
    }
}