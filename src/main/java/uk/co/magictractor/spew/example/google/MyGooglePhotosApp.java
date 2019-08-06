package uk.co.magictractor.spew.example.google;

import uk.co.magictractor.spew.api.OAuth2Application;
import uk.co.magictractor.spew.api.OAuth2ServiceProvider;
import uk.co.magictractor.spew.provider.google.Google;

public class MyGooglePhotosApp implements OAuth2Application {

    @Override
    public OAuth2ServiceProvider getServiceProvider() {
        return Google.getInstance();
    }

    @Override
    public String getScope() {
        // return "https://www.googleapis.com/auth/photoslibrary";

        // Do not need https://www.googleapis.com/auth/photoslibrary.sharing
        // just use sharedAlbums/list to get list of shared albums
        // Ah! do want album share info in order to get share Url
        return "https://www.googleapis.com/auth/photoslibrary https://www.googleapis.com/auth/photoslibrary.sharing";

        // just sharing results in permission denied when listing albums
        // return "https://www.googleapis.com/auth/photoslibrary.sharing";
    }

}
