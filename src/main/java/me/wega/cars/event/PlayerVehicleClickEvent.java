package me.wega.cars.event;

import me.wega.cars.vehicle.Vehicle;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class PlayerVehicleClickEvent extends PlayerEvent implements Cancellable {

    private final @NotNull Vehicle vehicle;
    private boolean cancelled;

    private static final HandlerList HANDLERS = new HandlerList();

    public PlayerVehicleClickEvent(@NotNull Player player, @NotNull Vehicle vehicle) {
        super(player);
        this.vehicle = vehicle;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
