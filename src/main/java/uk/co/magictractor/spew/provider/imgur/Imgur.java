package uk.co.magictractor.spew.provider.imgur;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.SpewOAuth2Configuration;
import uk.co.magictractor.spew.api.SpewOAuth2ConfigurationBuilder;
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
    public String oauth2AuthorizationUri() {
        return "https://api.imgur.com/oauth2/authorize";
    }

    @Override
    public String oauth2TokenUri() {
        return "https://api.imgur.com/oauth2/token";
    }

    @Override
    public void initConnectionConfigurationBuilder(SpewOAuth2ConfigurationBuilder builder) {

        // Imgur uses "pin" response_type for out of band values (copy and paste code)
        builder.withModifyAuthRequest(this::modifyAuthorizationRequest);
        builder.withModifyTokenRequest(this::modifyTokenRequest);
    }

    private void modifyAuthorizationRequest(OutgoingHttpRequest request, SpewOAuth2Configuration configuration) {

        System.err.println("modifyAuthorizationRequest: " + request);
        Map<String, String> queryParams = request.getQueryStringParams();
        if (queryParams.get("redirect_uri").equals(configuration.getOutOfBandUri())) {
            // Imgur uses "pin" response_type rather than "urn:ietf:wg:oauth:2.0:oob"
            request.setQueryStringParam("response_type", "pin");
            request.setQueryStringParam("redirect_uri", null);
        }
    }

    private void modifyTokenRequest(OutgoingHttpRequest request, Map<String, String> bodyData,
            SpewOAuth2Configuration configuration) {
        System.err.println("modifyTokenRequest: " + request);
        // Aaah... now dealing with OutgoingHttpRequest rather than ApplicationRequest, so no longer have the body params...
        if (bodyData.get("redirect_uri").equals(configuration.getOutOfBandUri())) {
            // Imgur uses "pin" response_type rather than "urn:ietf:wg:oauth:2.0:oob"
            bodyData.put("grant_type", "pin");
            String code = bodyData.remove("code");
            bodyData.put("pin", code);
        }
    }

    @Override
    public List<SpewTypeAdapter<?>> getTypeAdapters() {
        return Arrays.asList(InstantTypeAdapter.EPOCH_SECONDS);
    }

}
// body={"data":[],"success":true,"status":200}
