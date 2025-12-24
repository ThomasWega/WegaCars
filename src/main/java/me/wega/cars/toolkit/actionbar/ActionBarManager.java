package me.wega.cars.toolkit.actionbar;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import lombok.Getter;
import me.wega.cars.WegaCars;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class ActionBarManager {

    private static final long ACTIONBAR_DURATION = 3000L;

    @Getter
    private final @NotNull Multimap<Player, ActionBarData> actionBarDataMap = MultimapBuilder
            .hashKeys()
            .treeSetValues(Comparator.comparingInt(ActionBarData::priority))
            .build();
    public ActionBarManager(@NotNull WegaCars plugin) {
        plugin.getTaskScheduler().schedule(new ActionBarTask(plugin, this));
    }

    public @NotNull ActionBarData sendActionBar(@NotNull Player player, @NotNull Component text, int priority) {
        return this.sendActionBar(player, text, priority, System.currentTimeMillis() + ACTIONBAR_DURATION);
    }

    public @NotNull ActionBarData sendActionBar(@NotNull Player player, @NotNull Component text, int priority, long expireTimestamp) {
        final ActionBarData data = new ActionBarData(priority, expireTimestamp, text);
        this.sendActionBar(player, data);

        return data;
    }

    public void sendActionBar(@NotNull Player player, @NotNull ActionBarData data) {
        this.actionBarDataMap.get(player).removeIf(actionBarData -> actionBarData.priority() == data.priority()); // remove the previous bar
        this.actionBarDataMap.put(player, data);
    }
}
