package uk.co.magictractor.spew.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO! bin this?
public class OAuthAuthResponse implements SpewResponse {

    private final Map<String, String> values = new HashMap<>();

    public OAuthAuthResponse(String response) {
        throw new UnsupportedOperationException("Figure out what is hitting this");

        //        for (String entry : response.split("&")) {
        //            String[] keyAndValue = entry.split("=");
        //            values.put(keyAndValue[0], keyAndValue[1]);
        //        }
    }

    @Override
    public <T> T getObject(String key, Class<T> type) {
        // TODO! perhaps handle int/date conversions etc
        return (T) values.get(key);
    }

    @Override
    public <T> List<T> getList(String key, Class<T> type) {
        throw new UnsupportedOperationException("Not a Json response");
    }

}
