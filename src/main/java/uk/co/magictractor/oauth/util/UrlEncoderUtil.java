package uk.co.magictractor.oauth.util;

import java.net.URLEncoder;

public final class UrlEncoderUtil {

    private UrlEncoderUtil() {
    }

    public static String paramEncode(String string) {
        // ah! this encodes space to "+", but want "%20" for OAuth
        // see https://stackoverflow.com/questions/9336267/url-encode-oauth-signature
        return ExceptionUtil.call(() -> URLEncoder.encode(string, "UTF-8"));
    }

    // https://stackoverflow.com/questions/5864954/java-and-rfc-3986-uri-encoding
    public static String oauthEncode(String string) {
        return paramEncode(string).replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }

}
