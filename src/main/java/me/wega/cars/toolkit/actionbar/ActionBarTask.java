package me.wega.cars.toolkit.actionbar;

import me.wega.cars.toolkit.date.CustomTimeUnit;
import me.wega.cars.toolkit.task.AbstractTask;
import me.wega.cars.toolkit.task.TaskConfiguration;
import me.wega.cars.toolkit.task.TaskContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class ActionBarTask extends AbstractTask {

    private final @NotNull ActionBarManager actionBarManager;
    public ActionBarTask(@NotNull JavaPlugin plugin, @NotNull ActionBarManager actionBarManager) {
        super(plugin);
        this.actionBarManager = actionBarManager;
    }

    @Override
    protected @NotNull TaskConfiguration createConfiguration() {
        return TaskConfiguration.builder()
                .timeUnit(CustomTimeUnit.TICKS)
                .interval(1L)
                .build();
    }

    @Override
    public void execute(@NotNull TaskContext context) {
        new HashSet<>(this.actionBarManager.getActionBarDataMap().entries()).stream()
                .filter(entry -> entry.getValue().expireTimestamp() < System.currentTimeMillis())
                .forEach(entry -> this.actionBarManager.getActionBarDataMap().remove(entry.getKey(), entry.getValue())); // clean up map

        this.actionBarManager.getActionBarDataMap().asMap().forEach((player, data) ->
                data.stream().findFirst().ifPresent(actionBarData -> player.sendActionBar(actionBarData.text())));
    }
}
