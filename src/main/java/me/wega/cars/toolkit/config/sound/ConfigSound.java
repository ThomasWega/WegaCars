package me.wega.cars.toolkit.config.sound;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents a sound specified in a config file.
 * Allows to play the sound to a player.
 *
 * @implNote To disable the sound, set the volume to 0.
 */
@SuppressWarnings("PatternValidation")
public record ConfigSound(@NotNull String sound, float volume, float pitch) {
    public void play(@NotNull Player player) {
        if (volume <= 0) return;
        player.playSound(Sound.sound(
                Key.key(sound),
                Sound.Source.PLAYER,
                volume,
                pitch
        ));
    }

    public void play(@NotNull Player player, @NotNull Location loc) {
        if (volume <= 0) return;
        player.playSound(Sound.sound(
                Key.key(sound),
                Sound.Source.PLAYER,
                volume,
                pitch
        ), loc.getX(), loc.getY(), loc.getZ());
    }

    public void play(@NotNull Location loc) {
        if (volume <= 0) return;
        loc.getWorld().playSound(Sound.sound(
                Key.key(sound),
                Sound.Source.PLAYER,
                volume,
                pitch
        ), loc.getX(), loc.getY(), loc.getZ());
    }
}
