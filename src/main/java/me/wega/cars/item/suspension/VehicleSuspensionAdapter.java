package me.wega.cars.item.suspension;

import com.google.gson.*;
import me.wega.cars.annotation.JsonAdapter;
import me.wega.cars.WegaCars;
import me.wega.cars.item.part.VehiclePart;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

@JsonAdapter(value = VehicleSuspension.class)
public class VehicleSuspensionAdapter implements JsonSerializer<VehicleSuspension>, JsonDeserializer<VehicleSuspension> {

    @Override
    public JsonElement serialize(VehicleSuspension suspension, Type type, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.addProperty("spring", suspension.spring().getItemId());
        json.addProperty("shock", suspension.shock().getItemId());

        return json;
    }

    @Override
    public VehicleSuspension deserialize(@NotNull JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = jsonElement.getAsJsonObject();

        final VehiclePart spring = WegaCars.INSTANCE.getVehiclePartManager().get(object.get("spring").getAsString());
        final VehiclePart shock = WegaCars.INSTANCE.getVehiclePartManager().get(object.get("shock").getAsString());

        return new VehicleSuspension(spring, shock);
    }
}
