package uk.co.magictractor.spew.provider.flickr.json;

import uk.co.magictractor.spew.core.typeadapter.SpewTypeAdapter;
import uk.co.magictractor.spew.photo.TagSet;
import uk.co.magictractor.spew.util.ExceptionUtil;

public class TagSetTypeAdapter implements SpewTypeAdapter<TagSet> {

    private static final TagSetTypeAdapter INSTANCE = new TagSetTypeAdapter();

    public static TagSetTypeAdapter getInstance() {
        return INSTANCE;
    }

    private TagSetTypeAdapter() {
    }

    @Override
    public Class<TagSet> getType() {
        return TagSet.class;
    }

    @Override
    public TagSet fromString(String valueAsString) {
        return new TagSet(valueAsString);
    }

    @Override
    public String toString(TagSet value) {
        throw ExceptionUtil.notYetImplemented();
    }

}
