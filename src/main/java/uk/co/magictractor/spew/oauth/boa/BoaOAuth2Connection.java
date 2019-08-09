package uk.co.magictractor.spew.oauth.boa;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.QueryStringDecoder;
import uk.co.magictractor.spew.access.AuthorizationHandler;
import uk.co.magictractor.spew.api.OAuth2Application;
import uk.co.magictractor.spew.api.OAuth2ServiceProvider;
import uk.co.magictractor.spew.api.SpewRequest;
import uk.co.magictractor.spew.api.SpewResponse;
import uk.co.magictractor.spew.api.VerificationInfo;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.store.EditableProperty;
import uk.co.magictractor.spew.store.UserPropertyStore;
import uk.co.magictractor.spew.util.BrowserUtil;
import uk.co.magictractor.spew.util.ContentTypeUtil;
import uk.co.magictractor.spew.util.ExceptionUtil;
import uk.co.magictractor.spew.util.spi.SPIUtil;

// https://tools.ietf.org/html/rfc6749
// https://developers.google.com/identity/protocols/OAuth2
public class BoaOAuth2Connection extends AbstractBoaOAuthConnection<OAuth2Application, OAuth2ServiceProvider> {

    /*
     * milliseconds to remove from expiry to ensure that we refresh if getting
     * close to the server's expiry time
     */
    private static final int EXPIRY_BUFFER = 60 * 1000;

    private final EditableProperty accessToken;
    // milliseconds since start of epoch
    private final EditableProperty accessTokenExpiry;
    private final EditableProperty refreshToken;

    /**
     * Default visibility, applications should obtain instances via
     * {@link BoaConnectionFactory#createConnection}, usually indirectly via
     * OAuthConnectionFactory.
     */
    /* default */ BoaOAuth2Connection(OAuth2Application application) {
        super(application);

        UserPropertyStore userPropertyStore = SPIUtil.firstAvailable(UserPropertyStore.class);
        this.accessToken = userPropertyStore.getProperty(application, "access_token");
        this.accessTokenExpiry = userPropertyStore.getProperty(application, "access_token_expiry");
        this.refreshToken = userPropertyStore.getProperty(application, "refresh_token");
    }

    @Override
    public SpewResponse request(SpewRequest apiRequest) {

        if (accessToken.getValue() == null) {
            // authenticateUser();
            authorize();
        }
        // TODO! restore this - see comments about refresh tokens, perhaps related
        //        else if (isAccessTokenExpired()) {
        //            fetchRefreshedAccessToken();
        //        }

        return ExceptionUtil.call(() -> request0(apiRequest, this::setAuthHeader));
    }

    private void setAuthHeader(SpewRequest request) {
        request.addHeader("Authorization", "Bearer " + accessToken.getValue());
    }

    // https://developers.google.com/photos/library/guides/authentication-authorization
    private void authorize() {
        OAuth2Application application = getApplication();

        SpewRequest request = application.createGetRequest(getServiceProvider().getAuthorizationUri());

        // GitHub returns application/x-www-form-urlencoded content type by default
        request.addHeader(ContentTypeUtil.ACCEPT_HEADER_NAME, ContentTypeUtil.JSON_MIME_TYPES.get(0));

        // A bit mucky. The callback value comes from the handler but is also used in the verification function.
        AuthorizationHandler[] authHandlerHolder = new AuthorizationHandler[1];
        AuthorizationHandler authHandler = application
                .getAuthorizationHandler(() -> (info -> verify(info, authHandlerHolder[0].getCallbackValue())));
        authHandlerHolder[0] = authHandler;

        authHandler.preOpenAuthorizationInBrowser(application);

        request.setQueryStringParam("client_id", application.getClientId());
        // Omit for Imgur with "pin"
        request.setQueryStringParam("redirect_uri", authHandler.getCallbackValue());

        // Always "code".
        // "token" type is more appropriate for client-side OAuth.
        // "pin" for Imgur
        request.setQueryStringParam("response_type", "code");

        request.setQueryStringParam("scope", application.getScope());

        application.modifyAuthorizationRequest(request);

        String authUri = request.getUrl();
        BrowserUtil.openBrowserTab(authUri);

        authHandler.postOpenAuthorizationInBrowser(application);
    }

    // TODO! set and check randomised "state" value : https://developers.google.com/identity/protocols/OpenIDConnect#server-flow
    private boolean verify(VerificationInfo verificationInfo, String callback) {
        boolean verified = false;
        try {
            fetchAccessAndRefreshToken(verificationInfo.getVerificationCode(), callback);
            verified = true;
        }
        catch (Exception e) {
            // Do nothing. verified is false.
            System.err.println("verification failed");
            e.printStackTrace(System.err);
        }

        return verified;
    }

    private void fetchAccessAndRefreshToken(String code, String callback) {
        OAuth2Application application = getApplication();

        // ah! needed to be POST else 404 (Google)
        SpewRequest request = application.createPostRequest(getServiceProvider().getTokenUri());

        // GitHub returns application/x-www-form-urlencoded content type by default
        request.addHeader(ContentTypeUtil.ACCEPT_HEADER_NAME, ContentTypeUtil.JSON_MIME_TYPES.get(0));

        request.setContentType(ContentTypeUtil.FORM_MIME_TYPE);

        request.setBodyParam("code", code);
        //request.setBodyParam("pin", code); // Imgur with "pin"
        request.setBodyParam("client_id", application.getClientId());
        request.setBodyParam("client_secret", application.getClientSecret());

        request.setBodyParam("grant_type", "authorization_code");
        //request.setBodyParam("grant_type", "pin"); // Imgur with "pin"

        // request.setParam("redirect_uri",
        // "https://www.googleapis.com/auth/photoslibrary");
        // request.setParam("redirect_uri", "https://magictractor.co.uk");

        // A 400 results if this does not match the original redirect URI.
        //request.setBodyParam("redirect_uri", "http://127.0.0.1:8080");
        // application.host() -> no could be OOB
        request.setBodyParam("redirect_uri", callback);
        // request.setParam("scope", "");

        application.modifyTokenRequest(request);

        System.err.println("request: " + request);

        SpewParsedResponse response = authRequest(request);
        System.err.println("response: " + response);

        // TODO! only get a refresh_token if auth request included access_type=offline
        // add that or not? perhaps make it configurable in application
        String refreshTokenValue = response.getString("refresh_token");
        if (refreshTokenValue != null) {
            refreshToken.setValue(refreshTokenValue);
        }

        // accessToken.setValue(response.getString("access_token"));
        // System.err.println("access_token set to " + accessToken.getValue());
        setAccessToken((key) -> response.getString(key));
    }

    // TODO! handle invalid/expired refresh tokens
    // https://developers.google.com/identity/protocols/OAuth2InstalledApp#offline
    private void fetchRefreshedAccessToken() {
        SpewRequest request = getApplication().createPostRequest(getServiceProvider().getTokenUri());

        request.setBodyParam("refresh_token", refreshToken.getValue());
        request.setBodyParam("client_id", getApplication().getClientId());
        request.setBodyParam("client_secret", getApplication().getClientSecret());

        request.setBodyParam("grant_type", "refresh_token");
        SpewParsedResponse response = authRequest(request);

        // accessToken.setValue(response.getString("access_token"));
        // System.err.println("accessToken refreshed to " + accessToken.getValue());

        setAccessToken((key) -> response.getString(key));
    }

    //  private void setAccessToken(OAuthResponse response) {
    //      setAccessToken(response.getString("access_token"), response.getString("expires_in"));
    //  }

    //
    private void setAccessToken(FullHttpMessage httpMessage) {
        ByteBuf content = httpMessage.content();
        // new QueryStringDecoder()

        String s = content.toString(Charsets.UTF_8);
        System.err.println("content: " + s);

        QueryStringDecoder d = new QueryStringDecoder(s, Charsets.UTF_8, false);
        Map<String, List<String>> parameters = d.parameters();
        System.err.println(parameters);

        setAccessToken((key) -> Iterables.getOnlyElement(parameters.get(key)));
    }

    private void setAccessToken(Function<String, String> valueMap) {
        accessToken.setValue(valueMap.apply("access_token"));

        // typically 3600 for one hour
        String expiresInSecondsString = valueMap.apply("expires_in");
        if (expiresInSecondsString != null) {
            int expiresInSeconds = Integer.parseInt(valueMap.apply("expires_in"));
            long expiresInMilliseconds = expiresInSeconds * 1000;
            long expiry = System.currentTimeMillis() + expiresInMilliseconds - EXPIRY_BUFFER;
            accessTokenExpiry.setValue(Long.toString(expiry));

            long diff = expiry - new Date().getTime();
            System.err.println("diff: " + diff);
        }

        // some service providers update the refresh token, others do not
        String newRefreshToken = valueMap.apply("refresh_token");
        if (newRefreshToken != null) {
            refreshToken.setValue(newRefreshToken);
        }

        System.err.println("accessToken set: " + accessToken.getValue() + " expires " + accessTokenExpiry.getValue());
    }

    private boolean isAccessTokenExpired() {
        long expiry = Long.parseLong(accessTokenExpiry.getValue());
        boolean isExpired = System.currentTimeMillis() - expiry > 0;

        if (isExpired) {
            System.err.println("accessToken has expired");
        }

        return isExpired;
    }

    //  {
    //        "access_token": "ya29.GltcBrSQ1GX2N6sN57ktc1smgmocYpP1MKgn_wPkJRpu0KcTxgDNW7r4UBg3w3rK0J6B3tQI-OjIgFuHDXBmY3a4--7Jj3qy6saDIYbrdYobv3jVxrMA4B3hEdGn",
    //        "expires_in": 3600,
    //        "refresh_token": "1/gLkG1sNlUr3U3T-TVWtX37jOe40f6eQvgoFLG_26mfs",
    //        "scope": "https://www.googleapis.com/auth/photoslibrary https://www.googleapis.com/auth/photoslibrary.sharing",
    //        "token_type": "Bearer"
    //      }

    private SpewParsedResponse authRequest(SpewRequest apiRequest) {
        // forAll(apiRequest);

        // TODO! more elegant way to prep the request rather than repeating this here?
        apiRequest.prepareToSend();

        SpewResponse response = ExceptionUtil.call(() -> request0(apiRequest));
        return SpewParsedResponse.parse(getApplication(), response);
    }

    //    @Override
    //    public SpewResponse request(ConnectionRequest request) {
    //        // TODO Auto-generated method stub
    //        return null;
    //    }

}
