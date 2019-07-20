package uk.co.magictractor.spew.api;

public interface OAuth1Application extends SpewApplication { // <OAuth1ServiceProvider> {

    @Override
    OAuth1ServiceProvider getServiceProvider();

    // Spring social calls this consumerKey
    String getAppToken();

    // Spring social calls this consumerSecret
    String getAppSecret();

}
