package uk.co.magictractor.spew.api;

public interface SpewOAuth1ServiceProvider extends SpewServiceProvider {

    String getTemporaryCredentialRequestUri();

    String getResourceOwnerAuthorizationUri();

    String getTokenRequestUri();

    String getRequestSignatureMethod();

    default String getJavaSignatureMethod() {
        // String requestSignatureMethod = getRequestSignatureMethod();
        switch (getRequestSignatureMethod()) {
            case "MD5":
                // MD5 is used by ImageBam - MD5 and HMAC-MD5 are not interchangable
                // return "MD5";
                return "HmacMD5";
            case "HMAC-SHA1":
                // HMAC-SHA1 is used by Flickr
                return "HmacSHA1";
            default:
                throw new IllegalStateException("Missing Java type corresponding to " + getRequestSignatureMethod()
                        + ". Override or modify getJavaSignatureMethod()");
        }
    }

    // and consumer key
}
