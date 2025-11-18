package me.wega.cars.toolkit.json.adapter;

import com.google.common.collect.BiMap;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.function.Supplier;

public class BiMapAdapter<K, V> implements JsonSerializer<BiMap<K, V>>, JsonDeserializer<BiMap<K, V>> {
    private final Supplier<BiMap<K, V>> biMapSupplier;
    private final Type keyType;
    private final Type valueType;

    public BiMapAdapter(Supplier<BiMap<K, V>> biMapSupplier, Type keyType, Type valueType) {
        this.biMapSupplier = biMapSupplier;
        this.keyType = keyType;
        this.valueType = valueType;
    }

    @Override
    public JsonElement serialize(BiMap<K, V> biMap, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray entriesArray = new JsonArray();
        
        for (K key : biMap.keySet()) {
            JsonObject entry = new JsonObject();
            entry.add("key", context.serialize(key, keyType));
            entry.add("value", context.serialize(biMap.get(key), valueType));
            entriesArray.add(entry);
        }
        
        JsonObject json = new JsonObject();
        json.add("entries", entriesArray);
        return json;
    }

    @Override
    public BiMap<K, V> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
            throws JsonParseException {
        BiMap<K, V> biMap = biMapSupplier.get();
        
        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray entriesArray = jsonObject.getAsJsonArray("entries");
        
        for (JsonElement element : entriesArray) {
            JsonObject entry = element.getAsJsonObject();
            K key = context.deserialize(entry.get("key"), keyType);
            V value = context.deserialize(entry.get("value"), valueType);
            biMap.put(key, value);
        }
        
        return biMap;
    }
}