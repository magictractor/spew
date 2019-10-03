package uk.co.magictractor.spew.provider.flickr;

import java.util.Arrays;
import java.util.List;

import uk.co.magictractor.spew.api.BadResponseException;
import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.SpewOAuth1ServiceProvider;
import uk.co.magictractor.spew.core.response.parser.SpewHttpMessageBodyReader;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponseBuilder;
import uk.co.magictractor.spew.core.typeadapter.BooleanTypeAdapter;
import uk.co.magictractor.spew.core.typeadapter.InstantTypeAdapter;
import uk.co.magictractor.spew.core.typeadapter.LocalDateTimeTypeAdapter;
import uk.co.magictractor.spew.core.typeadapter.SpewTypeAdapter;
import uk.co.magictractor.spew.provider.flickr.json.TagSetTypeAdapter;

// https://www.flickr.com/services/apps/create/
public class Flickr implements SpewOAuth1ServiceProvider {

    public static final String REST_ENDPOINT = "https://api.flickr.com/services/rest";

    private Flickr() {
    }

    // TODO! stop these being added to auth calls
    @Override
    public void prepareRequest(OutgoingHttpRequest request) {
        request.setQueryStringParam("format", "json");
        request.setQueryStringParam("nojsoncallback", "1");
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
    public List<SpewTypeAdapter<?>> getTypeAdapters() {
        return Arrays.asList(
            BooleanTypeAdapter.ZERO_AND_ONE,
            new LocalDateTimeTypeAdapter("yyyy-MM-dd HH:mm:ss"),
            InstantTypeAdapter.EPOCH_SECONDS,
            TagSetTypeAdapter.getInstance());
    }

    // {"stat":"fail","code":98,"message":"Invalid auth token"}}
    //
    // <rsp stat="ok">
    // <rsp stat="fail">
    // <err code="98" msg="Invalid auth token" />
    // </rsp>
    @Override
    public void buildParsedResponse(SpewParsedResponseBuilder parsedResponseBuilder) {
        SpewHttpMessageBodyReader bodyReader = parsedResponseBuilder.getBodyReader();
        String status = bodyReader.getString("stat");
        if (!"ok".equals(status)) {
            String errorCode = bodyReader.getString("code");
            String errorMessage = bodyReader.getString("message");

            if ("98".equals(errorCode)) {
                // Bad auth. Change status to trigger reauthorization.
                parsedResponseBuilder.withStatus(401);
            }
            else if ("105".equals(errorCode)) {
                // Server side error. Change status to trigger retry.
                parsedResponseBuilder.withStatus(500);
            }
            else {
                // Something else.
                throw new BadResponseException(status, errorCode, errorMessage);
            }
        }

        // Verification has just been done, so need verifier.
        parsedResponseBuilder.withoutVerification();
    }

}
