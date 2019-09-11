package uk.co.magictractor.spew.photo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.MoreObjects;

/**
 * A TagSet contains the Tags associated with a Photo. A Tag may only appear in
 * a TagSet once (like a java.util.Set), but the order of Tags in a TagSet is
 * considered when checking for equality.
 */
// TODO! rename since some behaviour is not like a java.util.Set.
public class TagSet {

    // add() has been modified to behave like a Set, but TagSets with different orders are not equal
    private final List<Tag> tags = new ArrayList<>();
    private final List<Tag> immutableTags = Collections.unmodifiableList(tags);

    /**
     * @param tagString space separated tags
     */
    // TODO! Flickr specific?
    public TagSet(String tagString) {

        for (String compactTagName : tagString.split(" ")) {

            // TODO! something here to check whether all aliases are included??
            Tag tag = Tag.fetchOrCreateTag(compactTagName);
            if (tag == null) {
                throw new IllegalStateException();
            }

            addTag(tag);
        }
    }

    public TagSet(TagSet cloneTagSet) {
        tags.addAll(cloneTagSet.tags);
    }

    public Tag getDeepestTag(String tagTypeName) {
        return getDeepestTag(TagType.fetch(tagTypeName));
    }

    // TODO! unknown tags will break this
    public Tag getDeepestTag(TagType tagType) {
        Tag result = null;
        for (Tag tag : tags) {
            if (tagType.equals(tag.getTagType()) && (result == null || result.getDepth() < tag.getDepth())) {
                result = tag;
            }
        }

        return result;
    }

    public void addTag(Tag tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public void removeTag(Tag tag) {
        boolean removed = tags.remove(tag);
        if (!removed) {
            throw new IllegalStateException("tag was not in the tag set");
        }
    }

    public Collection<Tag> getTags() {
        return immutableTags;
    }

    public void sort(Comparator<Tag> comparator) {
        tags.sort(comparator);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TagSet)) {
            return false;
        }
        return ((TagSet) other).tags.equals(tags);
    }

    @Override
    public int hashCode() {
        return tags.hashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("tags", tags).toString();
    }
}
