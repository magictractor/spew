package uk.co.magictractor.oauth.api;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import uk.co.magictractor.oauth.json.JaywayConfiguration;

// https://stackoverflow.com/questions/34111276/jsonpath-with-jackson-or-gson
// https://github.com/json-path/JsonPath
public class OAuthJsonResponse implements OAuthResponse {

	// where should this live?
	static {
		Configuration.setDefaults(new JaywayConfiguration());
	}

	private ReadContext ctx;

	public OAuthJsonResponse(String response) {
		ctx = JsonPath.parse(response);
	}

	@Override
	public <T> T getObject(String key, Class<T> type) {
		return ctx.read(key, type);
	}

}
