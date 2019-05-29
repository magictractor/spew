package uk.co.magictractor.oauth.flickr;

import java.time.Instant;
import java.time.LocalDateTime;

import com.google.gson.GsonBuilder;

import uk.co.magictractor.oauth.api.OAuth1ServiceProvider;
import uk.co.magictractor.oauth.common.TagSet;
import uk.co.magictractor.oauth.flickr.json.TagSetTypeAdapter;
import uk.co.magictractor.oauth.json.BooleanTypeAdapter;
import uk.co.magictractor.oauth.json.InstantTypeAdapter;
import uk.co.magictractor.oauth.json.LocalDateTimeTypeAdapter;

// https://www.flickr.com/services/apps/create/
public class Flickr implements OAuth1ServiceProvider {

    public static final String REST_ENDPOINT = "https://api.flickr.com/services/rest/";

    //	https://api.imgur.com/oauth2/addclient
    //		https://api.imgur.com/oauth2/authorize
    //		https://api.imgur.com/oauth2/token

    private static final Flickr INSTANCE = new Flickr();

    private Flickr() {
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
}
