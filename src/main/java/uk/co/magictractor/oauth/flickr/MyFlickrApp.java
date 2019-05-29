package uk.co.magictractor.oauth.flickr;

import uk.co.magictractor.oauth.api.OAuth1Application;
import uk.co.magictractor.oauth.api.OAuth1Connection;
import uk.co.magictractor.oauth.api.OAuth1ServiceProvider;
import uk.co.magictractor.oauth.api.OAuthConnection;
import uk.co.magictractor.oauth.processor.properties.ResourceFileProperties;

public class MyFlickrApp implements OAuth1Application {

    private static final MyFlickrApp INSTANCE = new MyFlickrApp();

    private final ResourceFileProperties properties = new ResourceFileProperties(MyFlickrApp.class);

    // TODO! want connection cache in a superclass
    private OAuth1Connection connection;

    private MyFlickrApp() {
    }

    public static MyFlickrApp getInstance() {
        return INSTANCE;
    }

    @Override
    public OAuth1ServiceProvider getServiceProvider() {
        return Flickr.getInstance();
    }

    @Override
    public OAuthConnection getConnection() {
        if (connection == null) {
            connection = new OAuth1Connection(this);
        }
        return connection;
    }

    @Override
    public String getAppToken() {
        return properties.getProperty("app_token");
    }

    @Override
    public String getAppSecret() {
        return properties.getProperty("app_secret");
    }
}
