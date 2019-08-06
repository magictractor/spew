package uk.co.magictractor.spew.example.imgur;

import uk.co.magictractor.spew.api.OAuth2Application;
import uk.co.magictractor.spew.api.OAuth2ServiceProvider;
import uk.co.magictractor.spew.processor.properties.ResourceFileProperties;
import uk.co.magictractor.spew.provider.imgur.Imgur;

public class MyImgurApp implements OAuth2Application {

    private final ResourceFileProperties properties = new ResourceFileProperties(MyImgurApp.class);

    @Override
    public OAuth2ServiceProvider getServiceProvider() {
        return Imgur.getInstance();
    }

    @Override
    public String getClientId() {
        return properties.getProperty("client_id");
    }

    @Override
    public String getClientSecret() {
        return properties.getProperty("client_secret");
    }
}
