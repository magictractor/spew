package uk.co.magictractor.spew.api;

// OAuth1 specification https://tools.ietf.org/html/rfc5849
@SpewAuthType("OAuth1")
public interface SpewOAuth1Application<SP extends SpewOAuth1ServiceProvider> extends SpewApplication<SP> {

    default SpewOAuth1Configuration getConfiguration() {
        return getServiceProvider()
                .oauth1ConfigurationBuilder()
                .withApplication(this)
                .build();
    }

}
