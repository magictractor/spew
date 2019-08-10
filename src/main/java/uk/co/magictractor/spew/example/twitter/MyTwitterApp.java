package uk.co.magictractor.spew.example.twitter;

import uk.co.magictractor.spew.api.SpewOAuth1Application;
import uk.co.magictractor.spew.api.SpewOAuth1ServiceProvider;
import uk.co.magictractor.spew.provider.twitter.Twitter;

public class MyTwitterApp implements SpewOAuth1Application {

    @Override
    public SpewOAuth1ServiceProvider getServiceProvider() {
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

}
