package uk.co.magictractor.spew.api;

public interface SpewOAuth2ServiceProvider extends SpewServiceProvider {

    String getAuthorizationUri();

    String getTokenUri();

    default void modifyAuthorizationRequest(OutgoingHttpRequest request) {
        // Do nothing.
    }

    default void modifyTokenRequest(OutgoingHttpRequest request) {
        // Do nothing.
    }

}
