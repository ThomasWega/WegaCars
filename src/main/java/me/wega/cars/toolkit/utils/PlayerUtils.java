package me.wega.cars.toolkit.utils;

import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerUtils {
    /**
     * Show the worldborder red borders to the specified player
     * @param player the player to show the vignette to
     * @param redAmount how red is the vignette (0.0 to 1.0)
     */
    public static void showRedVignette(@NotNull Player player, float redAmount) {
        if (player.getWorldBorder() != null && !player.getWorldBorder().equals(player.getWorld().getWorldBorder()))
            return;

        final WorldBorder border = Bukkit.createWorldBorder();
        border.setCenter(player.getLocation().x(), player.getLocation().z());

        final double baseSize = 10_000_000;
        border.setSize(baseSize);

        border.setWarningDistance((int) Math.floor(redAmount * (baseSize * 10)));
        border.setWarningTime(0);

        player.setWorldBorder(border);
    }
}
