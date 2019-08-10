package uk.co.magictractor.spew.example.imgur;

import uk.co.magictractor.spew.api.SpewOAuth2Application;
import uk.co.magictractor.spew.api.SpewOAuth2ServiceProvider;
import uk.co.magictractor.spew.provider.imgur.Imgur;

public class MyImgurApp implements SpewOAuth2Application {

    @Override
    public SpewOAuth2ServiceProvider getServiceProvider() {
        return Imgur.getInstance();
    }

}
