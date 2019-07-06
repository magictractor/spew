package uk.co.magictractor.spew.json;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class LocalDateTimeTypeAdapter implements JsonDeserializer<LocalDateTime> {

    private final DateTimeFormatter formatter;

    public LocalDateTimeTypeAdapter(String format) {
        // TODO! Locale
        // Google has time zone "2018-11-20T15:09:42Z"
        // Flickr: "yyyy-MM-dd HH:mm:ss"
        formatter = new DateTimeFormatterBuilder().appendPattern(format).toFormatter();
    }

    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        String value = json.getAsString();
        return formatter.parse(value, LocalDateTime::from);
    }
}
