package uk.co.magictractor.spew.api;

import uk.co.magictractor.spew.api.connection.HasConnectionConfigurationBuilder;

// OAuth1 specification https://tools.ietf.org/html/rfc5849
@SpewAuthType("OAuth1")
public interface SpewOAuth1Application<SP extends SpewOAuth1ServiceProvider>
        extends SpewApplication<SP>, HasConnectionConfigurationBuilder<SpewOAuth1ConfigurationBuilder> {

    default SpewOAuth1Configuration createConfiguration() {
        return new SpewOAuth1ConfigurationBuilder()
                .withApplication(this)
                .build();
    }

}
