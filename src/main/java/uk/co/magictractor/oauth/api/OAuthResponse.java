package uk.co.magictractor.oauth.api;

import com.jayway.jsonpath.TypeRef;

public interface OAuthResponse {

    <T> T getObject(String key, Class<T> type);

    // hmm, don't want this here - it's Json specific
    <T> T getObject(String key, TypeRef<? extends T> type);

    default Object getObject(String key) {
        return getObject(key, Object.class);
    }

    default String getString(String key) {
        return getObject(key, String.class);
    }
}
