package uk.co.magictractor.spew.api;

import java.util.List;

public interface SpewResponse {

    <T> T getObject(String key, Class<T> type);

    <T> List<T> getList(String key, Class<T> type);

    default Object getObject(String key) {
        return getObject(key, Object.class);
    }

    default String getString(String key) {
        return getObject(key, String.class);
    }
}
