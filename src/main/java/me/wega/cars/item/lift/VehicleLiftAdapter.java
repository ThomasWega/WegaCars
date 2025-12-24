package me.wega.cars.item.lift;

import com.google.gson.*;
import me.wega.cars.annotation.JsonAdapter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

@JsonAdapter(value = VehicleLift.class)
public class VehicleLiftAdapter implements JsonSerializer<VehicleLift>, JsonDeserializer<VehicleLift> {

    @Override
    public JsonElement serialize(VehicleLift lift, Type type, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.add("location", context.serialize(lift.getLiftLocation(), Location.class));

        return json;
    }

    @Override
    public VehicleLift deserialize(@NotNull JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = jsonElement.getAsJsonObject();

        final Location location = context.deserialize(object.get("location"), Location.class);

        return new VehicleLift(location);
    }
}
