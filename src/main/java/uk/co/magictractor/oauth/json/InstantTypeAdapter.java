package uk.co.magictractor.oauth.json;

import java.lang.reflect.Type;
import java.time.Instant;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class InstantTypeAdapter implements JsonDeserializer<Instant> {

	public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		// String value = json.getAsString();

		// aah! should use LocalDateTime for the time taken (no info about time zone is
		// recorded)
//		if (value.contains(" ")) {
//			// Assume date time as formatted string
//			// datetaken=2018-06-23 13:52:33
//			// "datetaken": "2018-08-09 16:08:38"
//		//	return null;
//			
//			// TODO! Locale
//			DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").toFormatter();
//			
//			//return formatter.parse(value, Instant::from);
//			return formatter.parse(value);
//		} else {
		// Assume milliseconds.
		long millis = json.getAsLong();
		return Instant.ofEpochMilli(millis);
		// }

	}
}
