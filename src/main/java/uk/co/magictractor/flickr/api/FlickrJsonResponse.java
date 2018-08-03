package uk.co.magictractor.flickr.api;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import uk.co.magictractor.flickr.json.JaywayConfiguration;

// https://stackoverflow.com/questions/34111276/jsonpath-with-jackson-or-gson
// https://github.com/json-path/JsonPath
public class FlickrJsonResponse implements FlickrResponse {

	// where shoudl this live?
	static {
		Configuration.setDefaults(new JaywayConfiguration());
	}

	// private Map<String, Object> values;
	private ReadContext ctx;

	public FlickrJsonResponse(String response) {
		// values = new Gson().fromJson(response, Map.class);
		ctx = JsonPath.parse(response);
	}

	@Override
	public <T> T getObject(String key, Class<T> type) {
		// return values.get(key);
		// TODO! add type here and create POJOs for common Flickr types (notably Photo)
		return ctx.read(key, type);
	}

}
