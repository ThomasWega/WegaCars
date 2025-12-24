package me.wega.cars.vehicle;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.wega.cars.annotation.JsonAdapter;
import me.wega.cars.WegaCars;
import me.wega.cars.item.part.VehiclePartType;
import me.wega.cars.item.suspension.VehicleSuspension;
import me.wega.cars.vehicle.holder.VehicleItemHolder;
import me.wega.cars.vehicle.holder.VehicleWheelHolder;
import me.wega.cars.vehicle.type.VehicleType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@JsonAdapter(value = Vehicle.class)
public class VehicleAdapter implements JsonSerializer<Vehicle>, JsonDeserializer<Vehicle> {

    private final Type vehicleItemMapType =
            new TypeToken<Map<VehiclePartType, VehicleItemHolder>>() {}.getType();

    private final Type vehicleWheelHolderListType =
            new TypeToken<List<VehicleWheelHolder>>() {}.getType();

    @Override
    public JsonElement serialize(Vehicle vehicle, Type type, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.add("uuid", context.serialize(vehicle.getUniqueId(), UUID.class));
        json.addProperty("type", vehicle.getVehicleType().getId());
        json.addProperty("jack_lift", vehicle.isJackLift());

        if (vehicle.getSuspension() != null)
            json.add("suspension", context.serialize(vehicle.getSuspension(), VehicleSuspension.class));

        json.add("wheels", context.serialize(vehicle.getTires(), vehicleWheelHolderListType));
        json.add("parts", context.serialize(vehicle.getParts(), vehicleItemMapType));
        json.addProperty("disassembled", vehicle.isDisassembled());
        json.addProperty("fuel", vehicle.getFuelRemaining());
        json.addProperty("drive_time", vehicle.getDriveTime());
        json.addProperty("tuning_value", vehicle.getTuningValue());
        if (vehicle.getFirstSparkplug() != null)
            json.addProperty("first_sparkplug", vehicle.getFirstSparkplug());

        return json;
    }

    @Override
    public Vehicle deserialize(@NotNull JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = jsonElement.getAsJsonObject();

        final UUID uuid = context.deserialize(object.get("uuid"), UUID.class);
        final VehicleType vehicleType = WegaCars.INSTANCE.getVehicleTypeManager().get(object.get("type").getAsString());

        final VehicleSuspension suspension = object.has("suspension") ? context.deserialize(object.get("suspension"), VehicleSuspension.class) : null;
        final List<VehicleWheelHolder> wheelHolders = context.deserialize(object.get("wheels"), vehicleWheelHolderListType);
        final Map<VehiclePartType, VehicleItemHolder> vehicleParts = context.deserialize(object.get("parts"), vehicleItemMapType);
        final boolean disassembled = object.get("disassembled").getAsBoolean();
        final int fuelRemaining = object.get("fuel").getAsInt();
        final boolean hasJackLift = object.get("jack_lift").getAsBoolean();
        final int driveTime = object.get("drive_time").getAsInt();
        final int tuningValue = object.get("tuning_value").getAsInt();
        final String firstSparkplug = object.has("first_sparkplug") ? object.get("first_sparkplug").getAsString() : null;

        final Vehicle vehicle = new Vehicle(uuid, vehicleType, wheelHolders, disassembled);
        vehicle.setSuspension(suspension);
        vehicle.setParts(vehicleParts);
        vehicle.setFuelRemaining(fuelRemaining);
        vehicle.setJackLift(hasJackLift);
        vehicle.setDriveTime(driveTime);
        vehicle.setTuningValue(tuningValue);
        if (firstSparkplug != null)
            vehicle.setFirstSparkplug(firstSparkplug);

        return vehicle;
    }
}
