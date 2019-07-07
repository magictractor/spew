package uk.co.magictractor.spew.api;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.TypeRef;

// https://stackoverflow.com/questions/34111276/jsonpath-with-jackson-or-gson
// https://github.com/json-path/JsonPath
public class OAuthJsonResponse implements SpewResponse {

    private ReadContext ctx;

    public OAuthJsonResponse(String response, Configuration jsonConfiguration) {
        ctx = JsonPath.parse(response, jsonConfiguration);
    }

    @Override
    public <T> T getObject(String key, Class<T> type) {
        return ctx.read(key, type);
    }

    @Override
    public <T> T getObject(String key, TypeRef<? extends T> type) {
        return ctx.read(key, type);
    }

}
