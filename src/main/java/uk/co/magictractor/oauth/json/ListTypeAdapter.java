package uk.co.magictractor.oauth.json;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

// used for Flickr tags
public class ListTypeAdapter implements JsonDeserializer<List<String>> {

	public List<String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		if (json.isJsonArray()) {
			return null;
		}

		String v = json.getAsString();

		return Arrays.asList(v.split(" "));
	}
}
