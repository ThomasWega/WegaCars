package me.wega.cars.toolkit.task;

import me.wega.cars.toolkit.date.CustomTimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class responsible for scheduling tasks based on their configuration.
 * Supports asynchronous and synchronous task scheduling with configurable initial delay,
 * interval, duration, and repetition settings.
 *
 * <p>Schedules tasks within the Bukkit server, allowing for both one-time and repeating tasks.
 * Handles completion and cancellation conditions based on the task's configuration.</p>
 */
public class TaskScheduler {
    private final Set<TaskContext> runningTasks = new HashSet<>();

    /**
     * Schedules the given {@link Task} according to its {@link TaskConfiguration}.
     * The task is executed either asynchronously or synchronously based on its configuration,
     * with optional initial delay, interval, and completion criteria.
     *
     * @param task the {@link Task} to be scheduled
     * @return a {@link TaskContext} providing runtime information and control over the task's execution
     */
    public @NotNull TaskContext schedule(final @NotNull Task task) {
        final TaskConfiguration config = task.getConfiguration();
        final CustomTimeUnit timeUnit = config.getTimeUnit();
        final JavaPlugin plugin = task.getPlugin();

        final TaskContext[] taskContext = new TaskContext[1];

        final Runnable runnable = () -> {
            final TaskContext context = taskContext[0];
            if (context == null) return; // Safety check

            // Check continuation condition
            if (config.getContinueCondition() != null && !config.getContinueCondition().getAsBoolean()) {
                context.cancelInternal();
                task.onComplete(context, Task.CompletionReason.CONDITION_FALSE);
                runningTasks.remove(context);
                return;
            }

            // Execute the task
            task.execute(context);
            context.incrementExecutionCount();

            // If no interval is set, treat it as a one-shot task
            if (config.getInterval() == null && config.getDuration() == null && config.getRepetitions() == null) {
                context.cancelInternal();
                task.onComplete(context, Task.CompletionReason.COMPLETED);
                runningTasks.remove(context);
                return;
            }

            // Check completion conditions
            final boolean repetitionsReached = config.getRepetitions() != null &&
                    context.getExecutionCount() >= config.getRepetitions();

            if (repetitionsReached) {
                context.cancelInternal();
                task.onComplete(context, Task.CompletionReason.REPETITIONS_REACHED);
                runningTasks.remove(context);
            }
        };

        BukkitTask bukkitTask;
        // Schedule based on configuration and create BukkitTask
        if (config.getInterval() == null) {
            if (config.isAsync())
                bukkitTask = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, timeUnit.toTicks(config.getInitialDelay()));
            else
                bukkitTask = Bukkit.getScheduler().runTaskLater(plugin, runnable, timeUnit.toTicks(config.getInitialDelay()));
        } else {
            if (config.isAsync()) {
                bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                        plugin,
                        runnable,
                        timeUnit.toTicks(config.getInitialDelay()),
                        timeUnit.toTicks(config.getInterval())
                );
            } else {
                bukkitTask = Bukkit.getScheduler().runTaskTimer(
                        plugin,
                        runnable,
                        timeUnit.toTicks(config.getInitialDelay()),
                        timeUnit.toTicks(config.getInterval())
                );
            }
        }

        // Create TaskContext immediately
        final TaskContext context = new TaskContext(task, bukkitTask);
        taskContext[0] = context;

        // Schedule duration-based completion if needed
        if (config.getDuration() != null) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (context.isCancelled()) return;
                context.cancelInternal();
                task.onComplete(context, Task.CompletionReason.DURATION_REACHED);
                runningTasks.remove(context);
            }, timeUnit.toTicks(config.getDuration()));
        }

        runningTasks.add(context);
        task.onStart(context); // Call onStart immediately after creation

        return context;
    }

    /**
     * Gets the set of currently running tasks.
     *
     * @return a set of running {@link TaskContext}s
     */
    @Unmodifiable
    public Set<TaskContext> getRunningTasks() {
        return Set.copyOf(runningTasks);
    }

    /**
     * Cancels all currently running tasks.
     */
    public void cancelAllTasks() {
        for (TaskContext context : runningTasks) {
            context.cancelInternal();
            context.getTask().onComplete(context, Task.CompletionReason.CANCELLED);
        }
        runningTasks.clear();
    }
}