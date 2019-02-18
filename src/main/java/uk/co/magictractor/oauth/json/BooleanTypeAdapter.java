package uk.co.magictractor.oauth.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class BooleanTypeAdapter implements JsonDeserializer<Boolean> {

	public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		int code = json.getAsInt();
		
		// TODO! throw exception for other values?
		return code == 0 ? false : code == 1 ? true : null;
	}
}
