package me.wega.cars.toolkit.actionbar;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public record ActionBarData(int priority, long expireTimestamp, @NotNull Component text) {
}
