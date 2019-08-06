package uk.co.magictractor.spew.example.imgur;

import uk.co.magictractor.spew.api.OAuth2Application;
import uk.co.magictractor.spew.api.OAuth2ServiceProvider;
import uk.co.magictractor.spew.provider.imgur.Imgur;

public class MyImgurApp implements OAuth2Application {

    @Override
    public OAuth2ServiceProvider getServiceProvider() {
        return Imgur.getInstance();
    }

}
