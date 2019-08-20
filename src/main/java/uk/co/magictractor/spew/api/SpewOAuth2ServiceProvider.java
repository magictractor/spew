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

    default String getOutOfBandUri() {
        // out-of-band isn't in the spec, but is supported by Google and other
        // https://mailarchive.ietf.org/arch/msg/oauth/OCeJLZCEtNb170Xy-C3uTVDIYjM
        return "urn:ietf:wg:oauth:2.0:oob";
    }

}
