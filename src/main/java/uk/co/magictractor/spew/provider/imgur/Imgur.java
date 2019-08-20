package uk.co.magictractor.spew.provider.imgur;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

import com.google.gson.GsonBuilder;

import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.SpewOAuth2ServiceProvider;
import uk.co.magictractor.spew.json.BooleanTypeAdapter;
import uk.co.magictractor.spew.json.InstantTypeAdapter;
import uk.co.magictractor.spew.json.LocalDateTimeTypeAdapter;
import uk.co.magictractor.spew.photo.TagSet;
import uk.co.magictractor.spew.provider.flickr.json.TagSetTypeAdapter;

/**
 * From https://imgur.com/tos: "don't use Imgur to host image libraries you link
 * to from elsewhere, content for your website, advertising, avatars, or
 * anything else that turns us into your content delivery network"
 */
// Imgur requires redirect values to be specified in the app
// https://imgur.com/account/settings/apps
// to display a code for copy&paste, Imgur requires a "pin" request type - currently only "code" is supported
public class Imgur implements SpewOAuth2ServiceProvider {

    public static final String REST_ENDPOINT = "https://api.imgur.com/3/";

    private Imgur() {
    }

    @Override
    public String getAuthorizationUri() {
        return "https://api.imgur.com/oauth2/authorize";
    }

    @Override
    public String getTokenUri() {
        return "https://api.imgur.com/oauth2/token";
    }

    @Override
    public void modifyAuthorizationRequest(OutgoingHttpRequest request) {
        System.err.println("modifyAuthorizationRequest: " + request);
        Map<String, String> queryParams = request.getQueryStringParams();
        if (queryParams.get("redirect_uri").equals(getOutOfBandUri())) {
            // Imgur uses "pin" response_type rather than "urn:ietf:wg:oauth:2.0:oob"
            queryParams.put("response_type", "pin");
            queryParams.remove("redirect_uri");
        }
    }

    @Override
    public void modifyTokenRequest(OutgoingHttpRequest request) {
        System.err.println("modifyTokenRequest: " + request);
        Map<String, Object> bodyParams = request.getBodyParams();
        if (bodyParams.get("redirect_uri").equals(getOutOfBandUri())) {
            // Imgur uses "pin" response_type rather than "urn:ietf:wg:oauth:2.0:oob"
            bodyParams.put("grant_type", "pin");
            Object code = bodyParams.remove("code");
            bodyParams.put("pin", code);
        }
    }

    @Override
    public GsonBuilder getGsonBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        // TODO! this option and perhaps other settings should be defaults for all
        // service providers
        // gsonBuilder.set

        gsonBuilder.registerTypeAdapter(boolean.class, new BooleanTypeAdapter());
        // TODO! Z shouldn't be quoted here?? - handle offset properly
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        gsonBuilder.registerTypeAdapter(Instant.class, InstantTypeAdapter.EPOCH_SECONDS);
        gsonBuilder.registerTypeAdapter(TagSet.class, new TagSetTypeAdapter());

        // gsonBuilder.registerTypeAdapter(List.class, new ListTypeAdapter());

        // gsonBuilder.registerTypeAdapterFactory(new FlickrTagsTypeAdapterFactory());

        return gsonBuilder;
    }
}
