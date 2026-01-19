package me.wega.cars.cmd.arg;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.wega.cars.WegaCars;
import me.wega.cars.VehiclesMessages;
import me.wega.cars.vehicle.type.VehicleType;
import me.wega.cars.vehicle.type.VehicleTypeManager;
import me.wega.cars.toolkit.utils.ColorUtils;
import org.jetbrains.annotations.Nullable;

public class VehicleTypeArgument extends CustomArgument<VehicleType, String> {
    private static final VehicleTypeManager TYPE_MANAGER = WegaCars.INSTANCE.getVehicleTypeManager();

    public VehicleTypeArgument() {
        super(new StringArgument("vehicle type"), info -> {
            final @Nullable VehicleType vehicleType = TYPE_MANAGER.get(info.input());
            if (vehicleType == null)
                throw CustomArgumentException.fromAdventureComponent(ColorUtils.color(
                        VehiclesMessages.INVALID_VEHICLE_TYPE.get()
                ));
            return vehicleType;
        });

        this.replaceSuggestions(ArgumentSuggestions.stringCollection(info ->
                TYPE_MANAGER.getKeys()
        ));
    }
}
