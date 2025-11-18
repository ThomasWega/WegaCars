package me.wega.cars.toolkit.utils;

import com.google.gson.*;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the conversion from JSON to other Types
 */
@UtilityClass
public class JSONUtils {

    /**
     * Checks if a JsonObject has a key and it's not null.
     *
     * @param jsonObject The JsonObject to check
     * @param key        The key to check
     * @return True if the key exists and is not null, false otherwise
     */
    public static boolean hasAndNotNull(@NotNull JsonObject jsonObject, @NotNull String key) {
        return jsonObject.has(key) && !jsonObject.get(key).isJsonNull();
    }

    /**
     * All data will be preserved (colors, events, ...)
     *
     * @param json Json to convert
     * @return Converted JSON to Component
     */
    public static @NotNull Component toComponent(@NotNull JsonObject json) {
        return GsonComponentSerializer.gson().deserialize(json.toString());
    }

    /**
     * Converts a JsonArray to a List of Strings.
     *
     * @param jsonArray The JsonArray to convert
     * @return A List of Strings containing the elements of the JsonArray
     */
    public static @NotNull List<@NotNull String> jsonArrayToList(@Nullable JsonArray jsonArray) {
        List<String> list = new ArrayList<>();
        if (jsonArray == null)
            return list;
        for (JsonElement element : jsonArray)
            list.add(element.getAsString());
        return list;
    }

    /**
     * Converts a List of Strings to a JsonArray.
     *
     * @param list The List of Strings to convert
     * @return A JsonArray containing the elements of the List
     */
    public static @NotNull JsonArray listToJsonArray(@Nullable List<String> list) {
        JsonArray jsonArray = new JsonArray();
        if (list == null)
            return jsonArray;
        for (String item : list)
            jsonArray.add(new JsonPrimitive(item));
        return jsonArray;
    }

    /**
     * Safely gets a String from a JsonObject.
     *
     * @param jsonObject   The JsonObject to get the string from
     * @param key          The key of the string in the JsonObject
     * @param defaultValue The default value to return if the key doesn't exist or isn't a string
     * @return The string value, or the default value if not found
     */
    @Contract("_, _, !null -> !null")
    public static @Nullable String getStringOrDefault(@NotNull JsonObject jsonObject, @NotNull String key, @Nullable String defaultValue) {
        JsonElement element = jsonObject.get(key);
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString())
            return element.getAsString();
        return defaultValue;
    }

    /**
     * Safely gets an int from a JsonObject.
     *
     * @param jsonObject   The JsonObject to get the int from
     * @param key          The key of the int in the JsonObject
     * @param defaultValue The default value to return if the key doesn't exist or isn't an int
     * @return The int value, or the default value if not found
     */
    @Contract("_, _, !null -> !null")
    public static @Nullable Integer getIntOrDefault(@NotNull JsonObject jsonObject, @NotNull String key, @Nullable Integer defaultValue) {
        JsonElement element = jsonObject.get(key);
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber())
            return element.getAsInt();
        return defaultValue;
    }

    public static List<String> mergeDefaultsFromResource(@NotNull Gson gson, @NotNull File file, @NotNull InputStream defaultResourceStream) throws IOException {
        final JsonObject defaultJson = JsonParser.parseReader(new InputStreamReader(defaultResourceStream)).getAsJsonObject();

        JsonObject existingJson;
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                existingJson = JsonParser.parseReader(reader).getAsJsonObject();
            }
        } else existingJson = new JsonObject();

        final List<String> addedKeys = new ArrayList<>();
        mergeJsonObjects(existingJson, defaultJson, "", addedKeys);

        final Gson prettyGson = gson.newBuilder()
                .setPrettyPrinting()
                .create();
        try (FileWriter writer = new FileWriter(file)) {
            prettyGson.toJson(existingJson, writer);
        }
        return addedKeys;
    }

    public static void mergeJsonObjects(@NotNull JsonObject target, @NotNull JsonObject defaults, @NotNull String path, @NotNull List<String> addedKeys) {
        for (String key : defaults.keySet()) {
            final String fullPath = path.isEmpty() ? key : path + "." + key;
            if (!target.has(key)) {
                target.add(key, defaults.get(key));
                addedKeys.add(fullPath);
            } else if (defaults.get(key).isJsonObject() && target.get(key).isJsonObject()) {
                mergeJsonObjects(target.getAsJsonObject(key), defaults.getAsJsonObject(key), fullPath, addedKeys);
            }
        }
    }
}