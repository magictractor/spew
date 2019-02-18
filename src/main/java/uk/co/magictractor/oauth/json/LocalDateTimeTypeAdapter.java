package uk.co.magictractor.oauth.json;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class LocalDateTimeTypeAdapter implements JsonDeserializer<LocalDateTime> {

	public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		String value = json.getAsString();

		// datetaken=2018-06-23 13:52:33
		// "datetaken": "2018-08-09 16:08:38"

		// TODO! Locale
		DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").toFormatter();

		// return formatter.parse(value, Instant::from);
		return formatter.parse(value, LocalDateTime::from);

	}
}
