package uk.co.magictractor.spew.example.flickr;

import uk.co.magictractor.spew.api.SpewOAuth1Application;
import uk.co.magictractor.spew.provider.flickr.Flickr;

public class MyFlickrApp implements SpewOAuth1Application<Flickr> {

    @Override
    public Flickr getServiceProvider() {
        return Flickr.getInstance();
    }

}
