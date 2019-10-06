package uk.co.magictractor.spew.example.google;

import uk.co.magictractor.spew.api.SpewApplicationCache;
import uk.co.magictractor.spew.api.SpewOAuth2Application;
import uk.co.magictractor.spew.provider.google.Google;

public class MyGooglePhotosApp implements SpewOAuth2Application<Google> {

    private static final MyGooglePhotosApp INSTANCE = SpewApplicationCache.add(MyGooglePhotosApp.class);

    public static MyGooglePhotosApp get() {
        return INSTANCE;
    }

    private MyGooglePhotosApp() {
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
