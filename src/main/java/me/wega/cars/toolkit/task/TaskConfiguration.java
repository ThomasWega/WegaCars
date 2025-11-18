package me.wega.cars.toolkit.task;

import me.wega.cars.toolkit.date.CustomTimeUnit;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

/**
 * Configuration class for defining task execution parameters.
 * Uses the builder pattern for convenient creation.
 */
@Value
@Builder(toBuilder = true)
public class TaskConfiguration {
    /**
     * Whether the task should run asynchronously.
     * False indicates the task will run on the main server thread.
     */
    boolean async;

    /**
     * The initial delay before the first execution of the task.
     * The time unit is specified by {@link #timeUnit}.
     */
    long initialDelay;

    /**
     * The interval between repeated executions of the task.
     * Null indicates the task should only run once.
     * The time unit is specified by {@link #timeUnit}.
     */
    @Nullable Long interval;

    /**
     * The number of times the task should execute.
     * Null indicates the task should run indefinitely or until another stopping condition is met.
     */
    @Nullable Long repetitions;

    /**
     * The total duration the task should run for.
     * Null indicates the task should run indefinitely or until another stopping condition is met.
     * The time unit is specified by {@link #timeUnit}.
     */
    @Nullable Long duration;

    /**
     * A condition that determines whether the task should continue executing.
     * The task will stop when this supplier returns false.
     * Null indicates no condition check is needed.
     */
    @Nullable BooleanSupplier continueCondition;

    /**
     * The time unit used for interpreting time-based fields.
     * Defaults to {@link CustomTimeUnit#SECONDS} if not specified.
     */
    @Builder.Default
    @NotNull CustomTimeUnit timeUnit = CustomTimeUnit.TICKS;
}