package uk.co.magictractor.spew.imagebam;

import com.google.gson.GsonBuilder;

import uk.co.magictractor.spew.api.BadResponseException;
import uk.co.magictractor.spew.api.OAuth1ServiceProvider;
import uk.co.magictractor.spew.api.SpewResponse;

/**
 * Meh, Imagebam doesn't look promising. API is patchy - looks like we can't
 * list all images (only list those in a given gallery), or move images to and
 * from a gallery.
 */
public class ImageBam implements OAuth1ServiceProvider {

    public static final String REST_ENDPOINT = "http://www.imagebam.com/sys/API/resource/";

    //	https://api.imgur.com/oauth2/addclient
    //		https://api.imgur.com/oauth2/authorize
    //		https://api.imgur.com/oauth2/token

    private static final ImageBam INSTANCE = new ImageBam();

    private ImageBam() {
    }

    public static ImageBam getInstance() {
        return INSTANCE;
    }

    @Override
    public String getTemporaryCredentialRequestUri() {
        return "http://www.imagebam.com/sys/oauth/request_token";
    }

    @Override
    public String getResourceOwnerAuthorizationUri() {
        return "http://www.imagebam.com/sys/oauth/authorize_token";
    }

    @Override
    public String getTokenRequestUri() {
        return "http://www.imagebam.com/sys/oauth/request_access_token";
    }

    @Override
    public String getRequestSignatureMethod() {
        return "MD5";
    }

    @Override
    public GsonBuilder getGsonBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        // gsonBuilder.registerTypeAdapter(boolean.class, new BooleanTypeAdapter());
        // gsonBuilder.registerTypeAdapter(LocalDateTime.class, new
        // LocalDateTimeTypeAdapter("yyyy-MM-dd HH:mm:ss"));
        // gsonBuilder.registerTypeAdapter(Instant.class,
        // InstantTypeAdapter.EPOCH_SECONDS);
        // gsonBuilder.registerTypeAdapter(TagSet.class, new TagSetTypeAdapter());

        return gsonBuilder;
    }

    // {"rsp":{"status":"fail","error_code":108,"error_message":"permission denied: gallery_id"}}
    @Override
    public void verifyResponse(SpewResponse response) {
        String status = response.getString("$.rsp.status");
        if (!"ok".equals(status)) {
            String errorCode = response.getString("$.rsp.error_code");
            String errorMessage = response.getString("$.rsp.error_message");
            throw new BadResponseException(status, errorCode, errorMessage);
        }
    }

}
