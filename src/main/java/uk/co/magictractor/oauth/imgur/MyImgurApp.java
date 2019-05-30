package uk.co.magictractor.oauth.imgur;

import uk.co.magictractor.oauth.api.OAuth2Application;
import uk.co.magictractor.oauth.api.OAuth2AuthorizeResponseType;
import uk.co.magictractor.oauth.api.OAuth2ServiceProvider;
import uk.co.magictractor.oauth.processor.properties.ResourceFileProperties;

public class MyImgurApp implements OAuth2Application {

    private final ResourceFileProperties properties = new ResourceFileProperties(MyImgurApp.class);

    @Override
    public OAuth2ServiceProvider getServiceProvider() {
        return Imgur.getInstance();
    }

    @Override
    public String getScope() {
        // TODO!
        // return "https://www.googleapis.com/auth/photoslibrary
        // https://www.googleapis.com/auth/photoslibrary.sharing";
        return null;
    }

    @Override
    public OAuth2AuthorizeResponseType defaultAuthorizeResponseType() {
        return OAuth2AuthorizeResponseType.TOKEN;
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
