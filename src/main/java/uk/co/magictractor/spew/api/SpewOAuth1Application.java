package uk.co.magictractor.spew.api;

import uk.co.magictractor.spew.store.ApplicationPropertyStore;
import uk.co.magictractor.spew.util.spi.SPIUtil;

// OAuth1 specification https://tools.ietf.org/html/rfc5849
@SpewAuthType("OAuth1")
public interface SpewOAuth1Application<SP extends SpewOAuth1ServiceProvider>
        extends SpewApplication<SP>, HasCallbackServer {

    default String getConsumerKey() {
        return SPIUtil.firstAvailable(ApplicationPropertyStore.class).getProperty(this, "consumer_key");
    }

    default String getConsumerSecret() {
        return SPIUtil.firstAvailable(ApplicationPropertyStore.class).getProperty(this, "consumer_secret");
    }

    @Override
    default String getOutOfBandUri() {
        return "oob";
    }

}
