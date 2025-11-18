package me.wega.cars.toolkit.json.adapter;

import com.google.gson.*;
import me.wega.cars.toolkit.utils.ItemStackUtils;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

public class ItemStackArrayAdapter implements JsonSerializer<ItemStack[]>, JsonDeserializer<ItemStack[]> {

    @Override
    public ItemStack[] deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return ItemStackUtils.fromNBTStringArray(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(ItemStack[] itemStacks, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(ItemStackUtils.toNBTStringArray(itemStacks));
    }
}