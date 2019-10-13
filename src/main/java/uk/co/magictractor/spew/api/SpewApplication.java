package uk.co.magictractor.spew.api;

import java.util.Map;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import uk.co.magictractor.spew.api.connection.SpewConnectionCache;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponseBuilder;
import uk.co.magictractor.spew.core.verification.AuthorizationHandler;
import uk.co.magictractor.spew.util.StringUtil;

public interface SpewApplication<SP extends SpewServiceProvider> extends HasProperties {

    /**
     * <p>
     * Unique id which permits web request handlers to fetch a connection from
     * the cache using an id. Note that different instances of the same
     * application would have the same id, so would share a connection.
     * </p>
     * <p>
     * The id permits information about the connection, such as application and
     * service provider names, to be displayed when verification succeeds or
     * fails by including the id in a query string.
     * </p>
     */
    default int getId() {
        // Base the id on the class to trigger an error if multiple instances of an application are added to the cache.
        return getClass().hashCode();
    }

    default String getName() {
        return StringUtil.wordify(getClass().getSimpleName());
    }

    default SP getServiceProvider() {
        return SpewServiceProviderCache.getOrCreateForApplication(this);
    }

    default SpewConnection getConnection() {
        return SpewConnectionCache.getOrCreateConnection(this);
    }

    default ApplicationRequest createRequest(String httpMethod, String url) {
        ApplicationRequest request = new ApplicationRequest(this, httpMethod, url);
        prepareRequest(request);
        return request;
    }

    default void prepareRequest(ApplicationRequest request) {
        getServiceProvider().prepareRequest(request);
    }

    default void buildParsedResponse(SpewParsedResponseBuilder parsedResponseBuilder) {
        getServiceProvider().buildParsedResponse(parsedResponseBuilder);
    }

    default ApplicationRequest createGetRequest(String url) {
        return createRequest("GET", url);
    }

    default ApplicationRequest createPostRequest(String url) {
        return createRequest("POST", url);
    }

    default ApplicationRequest createDelRequest(String url) {
        return createRequest("DEL", url);
    }

    default AuthorizationHandler createAuthorizationHandler(SpewApplication<?> application) {
        // ...aah this creates a new instance, but they can be stateful (redirect_uri built from local server config)
        return getServiceProvider().createDefaultAuthorizationHandler(application);
    }

    /**
     * The out-of-band URI (sometimes referred to as "oob") is a special value
     * used in the redirect_url to tell the server to display a verification
     * code rather than perform a callback.
     */
    // hmm, move this? not right for basic auth / no auth - maybe subinterface for verified apps?
    // not used by BoaOAuth2??
    String getOutOfBandUri();

    @Override
    default void addProperties(Map<String, Object> properties) {
        properties.put("Application name", getName());
        properties.put("Service provider name", getServiceProvider().getName());
        properties.put("Authorization type", SpewAuthTypeUtil.getAuthType(getClass()));

    }

    public static ToStringHelper toStringHelper(SpewApplication<?> application) {
        ToStringHelper helper = MoreObjects.toStringHelper(application)
                .add("id", application.getId())
                .add("name", application.getName());

        return helper;
    }

}
