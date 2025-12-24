package me.wega.cars.vehicle.type;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.wega.cars.annotation.JsonAdapter;
import me.wega.cars.item.part.VehiclePartType;
import me.wega.cars.item.part.impl.VehicleShock;
import me.wega.cars.item.part.impl.VehicleTurbo;
import me.wega.cars.vehicle.VehicleStat;
import me.wega.cars.vehicle.enums.VehicleAlignmentCombination;
import me.wega.cars.vehicle.holder.VehicleInteractionData;
import me.wega.cars.vehicle.holder.VehicleTireData;
import me.wega.cars.vehicle.transmission.TransmissionType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonAdapter(value = VehicleType.class)
public class VehicleTypeAdapter implements JsonDeserializer<VehicleType> {

    private final Type vehicleTireDataListType =
            new TypeToken<List<VehicleTireData>>() {}.getType();

    @Override
    public VehicleType deserialize(@NotNull JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = jsonElement.getAsJsonObject();

        final String id = object.get("id").getAsString();
        final String vehicleName = object.get("name").getAsString();
        final String assembledModel = object.get("assembled-model").getAsString();
        final String disassembledModel = object.get("disassembled-model").getAsString();
        final String jackLiftModel = object.get("jack-lift-model").getAsString();
        final VehicleInteractionData driverOffset = context.deserialize(object.get("driver_offset"), VehicleInteractionData.class);
        final VehicleInteractionData hoodOffset = context.deserialize(object.get("hood_offset"), VehicleInteractionData.class);
        final VehicleInteractionData trunkOffset = context.deserialize(object.get("trunk_offset"), VehicleInteractionData.class);

        final TransmissionType transmissionType = TransmissionType.valueOf(object.get("transmission_type").getAsString());

        final Map<VehiclePartType, Vector> offsetMap = new HashMap<>();
        if (object.has("offsets")) {
            for (Map.Entry<String, JsonElement> entry : object.getAsJsonObject("offsets").entrySet()) {
                final VehiclePartType itemType = VehiclePartType.valueOf(entry.getKey());

                final JsonObject vectorObject = entry.getValue().getAsJsonObject();
                final Vector vector = createVector(vectorObject);
                offsetMap.put(itemType, vector);
            }
        }

        final List<VehicleTireData> tireVectors = context.deserialize(object.get("tires"), vehicleTireDataListType);

        final Table<VehicleAlignmentCombination, VehicleStat, Integer> alignmentTable = HashBasedTable.create();
        if (object.has("alignments")) {
            for (Map.Entry<String, JsonElement> entry : object.getAsJsonObject("alignments").entrySet()) {
                final VehicleAlignmentCombination alignment = VehicleAlignmentCombination.valueOf(entry.getKey());

                for (Map.Entry<String, JsonElement> statEntry : entry.getValue().getAsJsonObject().entrySet()) {
                    final VehicleStat vehicleStat = VehicleStat.valueOf(statEntry.getKey());
                    final int value = statEntry.getValue().getAsInt();

                    alignmentTable.put(alignment, vehicleStat, value);
                }
            }
        }

        final VehicleTurbo.TurboType turboType = VehicleTurbo.TurboType.valueOf(object.get("turbo-type").getAsString());
        final VehicleShock.ShockType shockType = VehicleShock.ShockType.valueOf(object.get("shock-type").getAsString());

        return new VehicleType(id, vehicleName, assembledModel, disassembledModel, jackLiftModel, driverOffset, hoodOffset, trunkOffset, transmissionType, offsetMap, tireVectors, alignmentTable, turboType, shockType);
    }

    private Vector createVector(JsonObject vectorObject) {
        final double x = vectorObject.get("x").getAsDouble();
        final double y = vectorObject.get("y").getAsDouble();
        final double z = vectorObject.get("z").getAsDouble();

        return new Vector(x, y, z);
    }
}
