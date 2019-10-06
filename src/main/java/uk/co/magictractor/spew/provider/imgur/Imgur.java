package uk.co.magictractor.spew.provider.imgur;

import java.util.Arrays;
import java.util.List;

import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.SpewOAuth2ServiceProvider;
import uk.co.magictractor.spew.core.typeadapter.InstantTypeAdapter;
import uk.co.magictractor.spew.core.typeadapter.SpewTypeAdapter;

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
        // Meh - this method needs refactored anyway - then I moved the oob and completely broke it.
        throw new UnsupportedOperationException("work in progress");

        //        System.err.println("modifyAuthorizationRequest: " + request);
        //        Map<String, String> queryParams = request.getQueryStringParams();
        //        if (queryParams.get("redirect_uri").equals(getOutOfBandUri())) {
        //            // Imgur uses "pin" response_type rather than "urn:ietf:wg:oauth:2.0:oob"
        //            queryParams.put("response_type", "pin");
        //            queryParams.remove("redirect_uri");
        //        }
    }

    @Override
    public void modifyTokenRequest(OutgoingHttpRequest request) {
        System.err.println("modifyTokenRequest: " + request);
        // Aaah... now dealing with OutgoingHttpRequest rather than ApplicationRequest, so no longer have the body params...
        throw new UnsupportedOperationException("broken by outgoing request rework");
        //        Map<String, Object> bodyParams = request.getBodyParams();
        //        if (bodyParams.get("redirect_uri").equals(getOutOfBandUri())) {
        //            // Imgur uses "pin" response_type rather than "urn:ietf:wg:oauth:2.0:oob"
        //            bodyParams.put("grant_type", "pin");
        //            Object code = bodyParams.remove("code");
        //            bodyParams.put("pin", code);
        //        }
    }

    @Override
    public List<SpewTypeAdapter<?>> getTypeAdapters() {
        return Arrays.asList(
            InstantTypeAdapter.EPOCH_SECONDS);
    }

}
// body={"data":[],"success":true,"status":200}
