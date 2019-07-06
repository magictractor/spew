package uk.co.magictractor.spew.json;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.function.Function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class InstantTypeAdapter implements JsonDeserializer<Instant> {

    private Function<JsonElement, Instant> converter;

    public static InstantTypeAdapter EPOCH_MILLIS = new InstantTypeAdapter(InstantTypeAdapter::convertEpochMillis);
    public static InstantTypeAdapter EPOCH_SECONDS = new InstantTypeAdapter(InstantTypeAdapter::convertEpochSeconds);

    private InstantTypeAdapter(Function<JsonElement, Instant> converter) {
        this.converter = converter;
    }

    public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return converter.apply(json);
    }

    private static Instant convertEpochMillis(JsonElement json) {
        long millis = json.getAsLong();
        return Instant.ofEpochMilli(millis);
    }

    private static Instant convertEpochSeconds(JsonElement json) {
        long millis = json.getAsLong();
        return Instant.ofEpochSecond(millis);
    }
}
