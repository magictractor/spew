package uk.co.magictractor.flickr.json;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.Set;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

// See https://github.com/json-path/JsonPath
public class JaywayConfiguration implements Configuration.Defaults {

	private final Gson gson = buildGson();
	private final JsonProvider jsonProvider = new GsonJsonProvider(gson);
	private final MappingProvider mappingProvider = new GsonMappingProvider(gson);

	@Override
	public JsonProvider jsonProvider() {
//		if (jsonProvider == null) {
//			jsonProvider = new GsonJsonProvider(buildGson());
//		}
		return jsonProvider;
	}

	// http://pragmateek.com/javajson-mapping-with-gson/#Naming_discrepancies
	private Gson buildGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.setFieldNamingStrategy(new FieldNamingStrategy() {
			@Override
			public String translateName(Field f) {
				// underscore seen in machine_tags
				return f.getName().toLowerCase().replace("_", "");
			}
		});
		builder.registerTypeAdapter(boolean.class, new BooleanTypeAdapter());
		builder.registerTypeAdapter(Boolean.class, new BooleanTypeAdapter());
		return builder.create();
	}

	@Override
	public MappingProvider mappingProvider() {
		return mappingProvider;
	}

	@Override
	public Set<Option> options() {
		return EnumSet.noneOf(Option.class);
	}

	private static class BooleanTypeAdapter implements JsonDeserializer<Boolean> {
		public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			int code = json.getAsInt();
			return code == 0 ? false : code == 1 ? true : null;
		}
	}
}
