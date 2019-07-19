package uk.co.magictractor.spew.util;

import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public final class UrlEncoderUtil {

    private UrlEncoderUtil() {
    }

    public static String queryString(String prefix, Map<String, Object> params) {
        return queryString(prefix, params, UrlEncoderUtil::paramEncode);
    }

    public static String queryString(String prefix, Map<String, Object> params,
            Function<String, String> paramValueEncodeFunction) {
        StringBuilder queryStringBuilder = new StringBuilder();
        appendQueryString(queryStringBuilder, prefix, params, paramValueEncodeFunction);
        return queryStringBuilder.toString();
    }

    public static void appendQueryString(StringBuilder queryStringBuilder, String prefix, Map<String, Object> params,
            Function<String, String> paramValueEncodeFunction) {

        boolean isFirstParam = true;
        for (Entry<String, Object> paramEntry : params.entrySet()) {
            if (isFirstParam) {
                isFirstParam = false;
                if (prefix != null) {
                    queryStringBuilder.append(prefix);
                }
            }
            else {
                queryStringBuilder.append("&");
            }
            queryStringBuilder.append(paramEntry.getKey());
            queryStringBuilder.append("=");
            queryStringBuilder.append(paramValueEncodeFunction.apply(paramEntry.getValue().toString()));
        }

    }

    public static String paramEncode(String string) {
        // This encodes space to "+", but want "%20" for OAuth
        // see https://stackoverflow.com/questions/9336267/url-encode-oauth-signature
        return ExceptionUtil.call(() -> URLEncoder.encode(string, "UTF-8"));
    }

    // https://stackoverflow.com/questions/5864954/java-and-rfc-3986-uri-encoding
    public static String oauthEncode(String string) {
        return paramEncode(string).replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }

}
