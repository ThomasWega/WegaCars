package me.wega.cars.toolkit.task.impl;

import me.wega.cars.toolkit.date.CustomTimeUnit;
import me.wega.cars.toolkit.task.AbstractTask;
import me.wega.cars.toolkit.task.Task;
import me.wega.cars.toolkit.task.TaskConfiguration;
import me.wega.cars.toolkit.task.TaskContext;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class CountdownTask extends AbstractTask {
    private final @NotNull Set<@NotNull Player> players;
    private final int seconds;
    private final int[] messagesAtSeconds;
    private final @NotNull Function<@NotNull Integer, @NotNull Component> messageSupplier;
    private final @Nullable Consumer<Task.@NotNull CompletionReason> onComplete;
    private int currentSeconds;

    public CountdownTask(@NotNull JavaPlugin plugin,
                         @NotNull Set<@NotNull Player> players,
                         int seconds,
                         int[] messagesAtSeconds,
                         @NotNull Function<@NotNull Integer, @NotNull Component> messageSupplier,
                         @Nullable Consumer<Task.@NotNull CompletionReason> onComplete) {
        super(plugin);
        this.players = players;
        this.seconds = seconds;
        this.currentSeconds = seconds;
        this.messagesAtSeconds = messagesAtSeconds;
        this.messageSupplier = messageSupplier;
        this.onComplete = onComplete;
    }

    @Override
    protected @NotNull TaskConfiguration createConfiguration() {
        return TaskConfiguration.builder()
                .async(true)
                .interval(1L)
                .timeUnit(CustomTimeUnit.SECONDS)
                .repetitions((long) seconds)
                .build();
    }

    @Override
    public void execute(@NotNull TaskContext context) {
        if (ArrayUtils.contains(messagesAtSeconds, currentSeconds)) {
            final Component msg = messageSupplier.apply(currentSeconds);
            players.stream()
                    .filter(Player::isOnline)
                    .forEach(player -> player.sendMessage(msg));
        }
        currentSeconds--;
    }

    @Override
    public void onComplete(@NotNull TaskContext context, @NotNull CompletionReason reason) {
        if (onComplete != null)
            onComplete.accept(reason);
    }
}