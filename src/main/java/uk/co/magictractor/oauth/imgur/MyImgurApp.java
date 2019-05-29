package uk.co.magictractor.oauth.imgur;

import uk.co.magictractor.oauth.api.OAuth2Application;
import uk.co.magictractor.oauth.api.OAuth2AuthorizeResponseType;
import uk.co.magictractor.oauth.api.OAuth2Connection;
import uk.co.magictractor.oauth.api.OAuth2ServiceProvider;
import uk.co.magictractor.oauth.api.OAuthConnection;
import uk.co.magictractor.oauth.processor.properties.ResourceFileProperties;

public class MyImgurApp implements OAuth2Application {

    private static final MyImgurApp INSTANCE = new MyImgurApp();

    private final ResourceFileProperties properties = new ResourceFileProperties(MyImgurApp.class);

    // TODO! common class for connection cache - see MyGooglePhotosApp
    private OAuth2Connection connection;

    private MyImgurApp() {
    }

    public static MyImgurApp getInstance() {
        return INSTANCE;
    }

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
    public OAuthConnection getConnection() {
        if (connection == null) {
            connection = new OAuth2Connection(this);
        }
        return connection;
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
        return properties.getProperty("client_id");
    }
}
