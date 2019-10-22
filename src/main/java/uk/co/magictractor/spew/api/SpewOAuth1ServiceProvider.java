package uk.co.magictractor.spew.api;

import uk.co.magictractor.spew.api.connection.HasConnectionConfigurationBuilder;

public interface SpewOAuth1ServiceProvider
        extends SpewServiceProvider, HasConnectionConfigurationBuilder<SpewOAuth1ConfigurationBuilder> {

    String oauth1TemporaryCredentialRequestUri();

    String oauth1ResourceOwnerAuthorizationUri();

    String oauth1TokenRequestUri();

    // TODO! config builder only for this
    String oauth1RequestSignatureMethod();

}
