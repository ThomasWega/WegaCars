package me.wega.cars.toolkit.json.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatterAdapter implements JsonDeserializer<DateTimeFormatter> {

    @Override
    public DateTimeFormatter deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        return DateTimeFormatter.ofPattern(jsonElement.getAsString());
    }
}