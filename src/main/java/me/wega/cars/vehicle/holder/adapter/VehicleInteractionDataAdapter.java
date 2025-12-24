package me.wega.cars.vehicle.holder.adapter;

import com.google.gson.*;
import me.wega.cars.annotation.JsonAdapter;
import me.wega.cars.vehicle.holder.VehicleInteractionData;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

@JsonAdapter(value = VehicleInteractionData.class)
public class VehicleInteractionDataAdapter implements JsonSerializer<VehicleInteractionData>, JsonDeserializer<VehicleInteractionData> {

    @Override
    public JsonElement serialize(VehicleInteractionData data, Type type, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.addProperty("x", data.vector().getX());
        json.addProperty("y", data.vector().getY());
        json.addProperty("z", data.vector().getZ());
        json.addProperty("width", data.width());
        json.addProperty("height", data.height());

        return json;
    }

    @Override
    public VehicleInteractionData deserialize(@NotNull JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = jsonElement.getAsJsonObject();

        final double x = object.get("x").getAsDouble();
        final double y = object.get("y").getAsDouble();
        final double z = object.get("z").getAsDouble();
        final float width = object.get("width").getAsFloat();
        final float height = object.get("height").getAsFloat();

        return new VehicleInteractionData(new Vector(x, y, z), width, height);
    }
}
