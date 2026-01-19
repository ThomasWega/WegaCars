package me.wega.cars.vehicle.holder.adapters;

import com.google.gson.*;
import me.wega.cars.annotation.JsonAdapter;
import me.wega.cars.WegaCars;
import me.wega.cars.item.part.VehiclePart;
import me.wega.cars.vehicle.holder.VehicleItemHolder;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

@JsonAdapter(value = VehicleItemHolder.class)
public class VehicleItemHolderAdapter implements JsonSerializer<VehicleItemHolder>, JsonDeserializer<VehicleItemHolder> {

    @Override
    public JsonElement serialize(VehicleItemHolder holder, Type type, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.addProperty("item", holder.getVehiclePart().getItemId());
        json.addProperty("durability", holder.getDurability());

        return json;
    }

    @Override
    public VehicleItemHolder deserialize(@NotNull JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = jsonElement.getAsJsonObject();

        final VehiclePart vehiclePart = WegaCars.INSTANCE.getVehiclePartManager().get(object.get("item").getAsString());
        final int durability = object.get("durability").getAsInt();

        return new VehicleItemHolder(vehiclePart, durability);
    }
}
