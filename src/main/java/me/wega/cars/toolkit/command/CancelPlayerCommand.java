package me.wega.cars.toolkit.command;

import me.wega.cars.WegaCars;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Utility class to cancel player commands based on predicates.
 */
@UtilityClass
public class CancelPlayerCommand {

    static {
        Bukkit.getPluginManager().registerEvents(new Listeners(), WegaCars.INSTANCE);
    }

    private static final Map<Predicate<Player>, @Nullable Component> COMMAND_PREDICATES = new HashMap<>();

    /**
     * Register a predicate that will be used to check if command execution should be cancelled.
     *
     * @param predicate the predicate to register
     * @param cancelMessage message to send when command is cancelled (can be null)
     */
    public static void registerPlayer(@NotNull Predicate<@NotNull Player> predicate, @Nullable Component cancelMessage) {
        COMMAND_PREDICATES.put(predicate, cancelMessage);
    }

    /**
     * Unregister a command cancellation predicate.
     *
     * @param predicate the predicate to unregister
     */
    public static void unregisterPlayer(@NotNull Predicate<@NotNull Player> predicate) {
        COMMAND_PREDICATES.remove(predicate);
    }

    public static void unregisterPlayer(@NotNull Player player) {
        COMMAND_PREDICATES.keySet().removeIf(predicate -> predicate.test(player));
    }

    private static class Listeners implements Listener {

        @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
        private void onCommand(PlayerCommandPreprocessEvent event) {
            Player player = event.getPlayer();

            COMMAND_PREDICATES.entrySet().stream()
                    .filter(entry -> entry.getKey().test(player))
                    .findFirst()
                    .ifPresent(entry -> {
                        event.setCancelled(true);
                        Component cancelMessage = entry.getValue();
                        if (cancelMessage != null) {
                            player.sendMessage(cancelMessage);
                        }
                    });
        }
    }
}
