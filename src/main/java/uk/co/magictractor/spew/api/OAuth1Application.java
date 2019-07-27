package uk.co.magictractor.spew.api;

public interface OAuth1Application extends SpewApplication { // <OAuth1ServiceProvider> {

    @Override
    OAuth1ServiceProvider getServiceProvider();

    String getConsumerKey();

    String getConsumerSecret();

}
