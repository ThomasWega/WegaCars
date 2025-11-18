package me.wega.cars.toolkit.json.adapter;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import me.wega.cars.toolkit.builder.ConfigItemBuilder;

import java.lang.reflect.Type;
import java.util.List;

public class ConfigItemBuilderAdapter implements JsonSerializer<ConfigItemBuilder>, JsonDeserializer<ConfigItemBuilder> {
    private final Type loreType = new TypeToken<List<String>>() {}.getType();

    @Override
    public JsonElement serialize(ConfigItemBuilder builder, Type type, JsonSerializationContext context) {
        JsonObject itemObject = new JsonObject();
        itemObject.addProperty("material", builder.getMaterial());
        itemObject.addProperty("display", builder.getDisplay());
        itemObject.add("lore", context.serialize(builder.getLore(), this.loreType));
        itemObject.addProperty("customModel", builder.getCustomModel());

        return itemObject;
    }

    @Override
    public ConfigItemBuilder deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject itemObject = jsonElement.getAsJsonObject();
        return new ConfigItemBuilder(itemObject);
    }
}