package uk.co.magictractor.oauth.api;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;

import uk.co.magictractor.oauth.json.JaywayConfiguration;

// https://stackoverflow.com/questions/34111276/jsonpath-with-jackson-or-gson
// https://github.com/json-path/JsonPath
public class OAuthJsonResponse implements OAuthResponse {

	// where should this live?
	static {
		Configuration.setDefaults(new JaywayConfiguration());
	}

	// private Map<String, Object> values;
	private ReadContext ctx;

	public OAuthJsonResponse(String response) {
		// values = new Gson().fromJson(response, Map.class);
		ctx = JsonPath.parse(response);
	}

	@Override
	public <T> T getObject(String key, Class<T> type) {
		// return values.get(key);
		// TODO! add type here and create POJOs for common Flickr types (notably Photo)
		//try {
			return ctx.read(key, type);
		//} catch (PathNotFoundException e) {
		//	// TODO! do what here??
		//	return null;
		//}
	}

}
