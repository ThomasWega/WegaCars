package me.wega.cars.item.part;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.*;
import me.wega.cars.annotation.JsonAdapter;
import me.wega.cars.item.part.impl.*;
import me.wega.cars.vehicle.VehicleStat;
import me.wega.cars.toolkit.builder.ConfigItemBuilder;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Map;

@JsonAdapter(value = VehiclePart.class, hierarchy = true)
public class VehiclePartAdapter implements JsonDeserializer<VehiclePart> {

    @Override
    public VehiclePart deserialize(@NotNull JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = jsonElement.getAsJsonObject();

        final String id = object.get("id").getAsString();
        final String partModel = object.get("model").getAsString();
        final VehiclePartType vehiclePartType = VehiclePartType.valueOf(object.get("type").getAsString());
        final int maxDurability = object.has("max-durability") ? object.get("max-durability").getAsInt() : 100; // just a default durability value
        final ConfigItemBuilder item = context.deserialize(object.get("item"), ConfigItemBuilder.class);

        return switch (vehiclePartType) {
            case ENGINE -> {
                final int topSpeed = object.get("top-speed").getAsInt();
                final int acceleration = object.get("acceleration").getAsInt();
                final int weight = object.get("weight").getAsInt();
                final int engineLife = object.get("engine-life").getAsInt();

                yield new VehicleEngine(id, partModel, maxDurability, item, topSpeed, acceleration, weight, engineLife);
            }
            case BRAKES -> {
                final int brakingForce = object.get("braking-force").getAsInt();
                final int unsprungWeight = object.get("unsprung-weight").getAsInt();

                yield new VehicleBrakes(id, partModel, maxDurability, item, brakingForce, unsprungWeight);
            }
            case SPARKPLUG -> {
                final int acceleration = object.get("acceleration").getAsInt();
                final int expectedLife = object.get("expected-life").getAsInt();
                final int fuelEfficiency = object.get("fuel-efficiency").getAsInt();

                yield new VehicleSparkplug(id, partModel, maxDurability, item, acceleration, expectedLife, fuelEfficiency);
            }
            case TURBO -> {
                final VehicleTurbo.TurboType turboType = VehicleTurbo.TurboType.valueOf(object.get("turbo-type").getAsString());
                final int acceleration = object.get("acceleration").getAsInt();
                final int topSpeed = object.get("top-speed").getAsInt();
                final int weight = object.get("weight").getAsInt();
                final int fuelEfficiency = object.get("fuel-efficiency").getAsInt();

                yield new VehicleTurbo(id, partModel, maxDurability, item, turboType, acceleration, topSpeed, weight, fuelEfficiency);
            }
            case WHEELS -> {
                final int handling = object.get("handling").getAsInt();
                final Table<String, VehicleStat, Integer> environmentTable = HashBasedTable.create();
                if (object.has("environment-stats")) {
                    for (Map.Entry<String, JsonElement> entry : object.getAsJsonObject("environment-stats").entrySet()) {
                        final String environment = entry.getKey();

                        for (Map.Entry<String, JsonElement> statEntry : entry.getValue().getAsJsonObject().entrySet()) {
                            final VehicleStat vehicleStat = VehicleStat.valueOf(statEntry.getKey());
                            final int value = statEntry.getValue().getAsInt();

                            environmentTable.put(environment, vehicleStat, value);
                        }
                    }
                }
                final String jackStandModel = object.get("jackstand-model").getAsString();

                yield new VehicleWheels(id, partModel, maxDurability, item, handling, environmentTable, jackStandModel);
            }
            case FUEL_TANK -> {
                final int tankCapacity = object.get("tank-capacity").getAsInt();

                yield new VehicleFuelTank(id, partModel, maxDurability, item, tankCapacity);
            }
            case SPRING -> {
                final int weightTolerance = object.get("weight-tolerance").getAsInt();
                final int unsprungWeight = object.get("unsprung-weight").getAsInt();

                yield new VehicleSpring(id, partModel, maxDurability, item, weightTolerance, unsprungWeight);
            }
            case SHOCK -> {
                final VehicleShock.ShockType shockType = VehicleShock.ShockType.valueOf(object.get("shock-type").getAsString());
                final int stability = object.get("stability").getAsInt();
                final int unsprungWeight = object.get("unsprung-weight").getAsInt();

                yield new VehicleShock(id, partModel, maxDurability, item, shockType, stability, unsprungWeight);
            }
        };
    }
}
