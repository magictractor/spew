package uk.co.magictractor.spew.provider.flickr;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

// bin?
public class FlickrTagsTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        System.err.println("gson: " + gson.toString() + ", type: " + type);

        return null;
    }

}
