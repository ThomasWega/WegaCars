package me.wega.cars.toolkit.task;

import lombok.Getter;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * Context object providing runtime information about a task's execution.
 * Includes utilities for tracking execution state and controlling the task.
 */
@Getter
public class TaskContext {
    private final @NotNull Task task;
    private final @NotNull BukkitTask bukkitTask;
    private final long startTime;
    /**
     * The number of times this task has been executed.
     */
    private long executionCount;

    /**
     * Creates a new task context.
     *
     * @param task The task instance being executed
     * @param bukkitTask The Bukkit task instance being executed
     */
    public TaskContext(final @NotNull Task task, final @NotNull BukkitTask bukkitTask) {
        this.task = task;
        this.bukkitTask = bukkitTask;
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Increments the execution count of this task.
     * Called internally by the task scheduler.
     */
    public void incrementExecutionCount() {
        this.executionCount++;
    }

    /**
     * Cancels the execution of this task.
     * If the task is already cancelled, this method has no effect.
     */
    public void cancel() {
        if (!this.isCancelled()) {
            bukkitTask.cancel();
            task.onComplete(this, Task.CompletionReason.CANCELLED);
        }
    }

    /**
     * Cancels the execution of this task without notifying the task.
     * This method is intended for internal use only.
     */
    void cancelInternal() {
        if (!this.isCancelled()) {
            bukkitTask.cancel();
        }
    }

    /**
     * Checks if this task has been cancelled.
     *
     * @return True if the task has been cancelled, false otherwise
     */
    public boolean isCancelled() {
        return bukkitTask.isCancelled();
    }

    /**
     * Gets the duration in milliseconds that this task has been running.
     *
     * @return The running duration in milliseconds
     */
    public long getRunningDuration() {
        return System.currentTimeMillis() - startTime;
    }
}