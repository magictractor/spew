package uk.co.magictractor.spew.twitter;

import uk.co.magictractor.spew.api.OAuth1Application;
import uk.co.magictractor.spew.api.OAuth1ServiceProvider;
import uk.co.magictractor.spew.processor.properties.ResourceFileProperties;

public class MyTwitterApp implements OAuth1Application {

    private final ResourceFileProperties properties = new ResourceFileProperties(MyTwitterApp.class);

    @Override
    public OAuth1ServiceProvider getServiceProvider() {
        return Twitter.getInstance();
    }

    //    @Override
    //    public String getScope() {
    //        // return "https://www.googleapis.com/auth/photoslibrary";
    //
    //        // Do not need https://www.googleapis.com/auth/photoslibrary.sharing
    //        // just use sharedAlbums/list to get list of shared albums
    //        // Ah! do want album share info in order to get share Url
    //        return "https://www.googleapis.com/auth/photoslibrary https://www.googleapis.com/auth/photoslibrary.sharing";
    //
    //        // just sharing results in permission denied when listing albums
    //        // return "https://www.googleapis.com/auth/photoslibrary.sharing";
    //    }

    @Override
    public String getAppToken() {
        // TODO! avoid duplicating these
        return properties.getProperty("app_token");
    }

    @Override
    public String getAppSecret() {
        return properties.getProperty("app_secret");
    }

}
