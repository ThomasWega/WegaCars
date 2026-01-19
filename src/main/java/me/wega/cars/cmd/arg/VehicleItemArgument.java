package me.wega.cars.cmd.arg;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.wega.cars.WegaCars;
import me.wega.cars.VehiclesMessages;
import me.wega.cars.item.part.VehiclePart;
import me.wega.cars.item.part.VehiclePartManager;
import me.wega.cars.toolkit.utils.ColorUtils;
import org.jetbrains.annotations.Nullable;

public class VehicleItemArgument extends CustomArgument<VehiclePart, String> {
    private static final VehiclePartManager ITEM_MANAGER = WegaCars.INSTANCE.getVehiclePartManager();

    public VehicleItemArgument() {
        super(new StringArgument("vehicle item"), info -> {
            final @Nullable VehiclePart vehiclePart = ITEM_MANAGER.get(info.input());
            if (vehiclePart == null)
                throw CustomArgumentException.fromAdventureComponent(ColorUtils.color(
                        VehiclesMessages.INVALID_VEHICLE_ITEM.get()
                ));
            return vehiclePart;
        });

        this.replaceSuggestions(ArgumentSuggestions.stringCollection(info ->
                ITEM_MANAGER.getKeys()
        ));
    }
}
