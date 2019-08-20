package uk.co.magictractor.spew.provider.flickr;

import java.time.Instant;
import java.time.LocalDateTime;

import com.google.gson.GsonBuilder;

import uk.co.magictractor.spew.api.BadResponseException;
import uk.co.magictractor.spew.api.SpewOAuth1ServiceProvider;
import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.json.BooleanTypeAdapter;
import uk.co.magictractor.spew.json.InstantTypeAdapter;
import uk.co.magictractor.spew.json.LocalDateTimeTypeAdapter;
import uk.co.magictractor.spew.photo.TagSet;
import uk.co.magictractor.spew.provider.flickr.json.TagSetTypeAdapter;

// https://www.flickr.com/services/apps/create/
public class Flickr implements SpewOAuth1ServiceProvider {

    public static final String REST_ENDPOINT = "https://api.flickr.com/services/rest";

    //	https://api.imgur.com/oauth2/addclient
    //		https://api.imgur.com/oauth2/authorize
    //		https://api.imgur.com/oauth2/token

    private static final Flickr INSTANCE = new Flickr();

    private Flickr() {
    }

    // TODO! stop these being added to auth calls
    @Override
    public void prepareRequest(OutgoingHttpRequest request) {
        request.setQueryStringParam("format", "json");
        request.setQueryStringParam("nojsoncallback", "1");
    }

    public static Flickr getInstance() {
        return INSTANCE;
    }

    @Override
    public String getTemporaryCredentialRequestUri() {
        return "https://www.flickr.com/services/oauth/request_token";
    }

    @Override
    public String getResourceOwnerAuthorizationUri() {
        // temporaryAuthToken added
        return "https://www.flickr.com/services/oauth/authorize?perms=write";
    }

    @Override
    public String getTokenRequestUri() {
        return "https://www.flickr.com/services/oauth/access_token";
    }

    @Override
    public String getRequestSignatureMethod() {
        return "HMAC-SHA1";
    }

    @Override
    public GsonBuilder getGsonBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        //		gsonBuilder.setFieldNamingStrategy(new FieldNamingStrategy() {
        //			@Override
        //			public String translateName(Field f) {
        //				// underscore seen in machine_tags
        //				return f.getName().toLowerCase().replace("_", "");
        //			}
        //		});
        gsonBuilder.registerTypeAdapter(boolean.class, new BooleanTypeAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter("yyyy-MM-dd HH:mm:ss"));
        gsonBuilder.registerTypeAdapter(Instant.class, InstantTypeAdapter.EPOCH_SECONDS);
        gsonBuilder.registerTypeAdapter(TagSet.class, new TagSetTypeAdapter());

        // gsonBuilder.registerTypeAdapter(List.class, new ListTypeAdapter());

        // gsonBuilder.registerTypeAdapterFactory(new FlickrTagsTypeAdapterFactory());

        return gsonBuilder;
    }

    // {"stat":"fail","code":1,"message":"User not found"}
    @Override
    public void verifyResponse(SpewParsedResponse response) {
        String status = response.getString("$.stat");
        if (!"ok".equals(status)) {
            String errorCode = response.getString("$.code");
            String errorMessage = response.getString("$.message");
            throw new BadResponseException(status, errorCode, errorMessage);
        }
    }
}
