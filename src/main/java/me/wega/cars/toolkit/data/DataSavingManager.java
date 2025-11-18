package me.wega.cars.toolkit.data;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages data saving operations across multiple modules to prevent thread overwhelming.
 * Uses a queue system to batch process save operations at regular intervals.
 */
@RequiredArgsConstructor
public class DataSavingManager {
    private static final Logger LOGGER = Logger.getLogger("DataSavingManager");
    private final Queue<SaveOperation> saveQueue = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final int batchSize;
    private final @NotNull Duration saveInterval;
    private boolean isRunning = false;

    /**
     * Represents a single save operation with its associated metadata
     */
    private record SaveOperation(@NotNull String operationId, @NotNull Supplier<Boolean> saveTask) {}

    public DataSavingManager() {
        this(1, Duration.ofSeconds(3));
    }

    /**
     * Starts the saving scheduler
     */
    public void start() {
        if (isRunning) return;

        isRunning = true;
        scheduler.scheduleAtFixedRate(this::processSaveQueue, 0, saveInterval.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Stops the saving scheduler and processes remaining operations
     */
    public void shutdown() {
        isRunning = false;
        scheduler.shutdown();

        // Process remaining operations
        while (!saveQueue.isEmpty())
            this.processSaveQueue();

        try {
            if (!scheduler.awaitTermination(1, TimeUnit.MINUTES))
                scheduler.shutdownNow();
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Queues a save operation
     * @param operationId The unique identifier for the operation
     * @param saveTask The save operation to perform
     */
    public void queueSave(@NotNull String operationId, @NotNull Supplier<Boolean> saveTask) {
        saveQueue.offer(new SaveOperation(operationId, saveTask));
    }

    /**
     * Processes the save queue in batches
     */
    private void processSaveQueue() {
        int processed = 0;

        while (!saveQueue.isEmpty() && processed < batchSize) {
            SaveOperation operation = saveQueue.poll();
            if (operation == null) break;

            try {
                boolean success = operation.saveTask.get();
                if (success)
                    LOGGER.info("Saved data for operation: " + operation.operationId());
                else
                    LOGGER.warning("Failed to save data for operation: " + operation.operationId());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error saving data for operation: " + operation.operationId(), e);
            }

            processed++;
        }
    }
}
