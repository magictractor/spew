package uk.co.magictractor.spew.flickr.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import uk.co.magictractor.spew.photo.TagSet;

public class TagSetTypeAdapter implements JsonDeserializer<TagSet> {

    public TagSet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return new TagSet(json.getAsString());
    }
}
