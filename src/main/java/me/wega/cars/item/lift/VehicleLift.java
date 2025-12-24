package me.wega.cars.item.lift;

import me.wega.cars.vehicle.Vehicle;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
@Getter
@Setter
public class VehicleLift {

    private final @NotNull Location liftLocation;
    private @Nullable Vehicle currentVehicle;

    public boolean hasVehicle() {
        return this.currentVehicle != null;
    }
}
