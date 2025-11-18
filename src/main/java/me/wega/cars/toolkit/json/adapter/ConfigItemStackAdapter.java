package me.wega.cars.toolkit.json.adapter;

import com.google.gson.*;
import me.wega.cars.toolkit.builder.ConfigItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

public class ConfigItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    @Override
    public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext context) {
        JsonObject itemObject = new JsonObject();

        // Add material
        itemObject.addProperty("material", itemStack.getType().name());

        // Add display name
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName())
            itemObject.addProperty("display", MiniMessage.miniMessage().serialize(itemStack.getItemMeta().displayName()));

        // Add lore
        JsonArray loreArray = new JsonArray();
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore())
            for (Component lore : itemStack.getItemMeta().lore())
                loreArray.add(MiniMessage.miniMessage().serialize(lore));

        itemObject.add("lore", loreArray);

        // Add custom model data
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasCustomModelData())
            itemObject.addProperty("custom-model", itemStack.getItemMeta().getCustomModelData());

        return itemObject;
    }

    @Override
    public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject itemObject = jsonElement.getAsJsonObject();
        return new ConfigItemBuilder(itemObject).build();
    }
}