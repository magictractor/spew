package uk.co.magictractor.oauth.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagSet {

    private final Set<Tag> tags;

    /**
     * @param tagString space separated tags
     */
    public TagSet(String tagString) {
        tags = new HashSet<>();

        for (String compactTagName : tagString.split(" ")) {

            Tag tag = Tag.fetchOrCreateTag(compactTagName);
            if (tag == null) {
                throw new IllegalStateException();
            }

            tags.add(tag);
        }
    }

    public TagSet(TagSet cloneTagSet) {
        tags = new HashSet<>(cloneTagSet.tags);
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

    public String getCompactTagNamesWithParents() {
        boolean first = true;
        StringBuilder compactTagNamesBuilder = new StringBuilder();
        for (Tag tag : getOrderedTagsWithParents()) {
            if (first) {
                first = false;
            }
            else {
                compactTagNamesBuilder.append(' ');
            }

            compactTagNamesBuilder.append(tag.getCompactTagName());
        }

        return compactTagNamesBuilder.toString();
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void removeTag(Tag tag) {
        boolean removed = tags.remove(tag);
        if (!removed) {
            throw new IllegalStateException("tag was not in the tag set");
        }
    }

    // TODO! order? unmodifiable?
    public Set<Tag> getTags() {
        return tags;
    }

    private List<Tag> getOrderedTagsWithParents() {
        Set<Tag> tagsWithParents = new HashSet<>(tags);

        for (Tag tag : tags) {
            Tag parentTag = tag.getParent();
            while (parentTag != null) {
                tagsWithParents.add(parentTag);
                parentTag = parentTag.getParent();
            }
        }

        List<Tag> orderedTagsWithParents = new ArrayList<>(tagsWithParents);
        orderedTagsWithParents.sort(TagComparator.ASCENDING);

        return orderedTagsWithParents;
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
        // TODO!
        return "Tags [tags=" + tags + "]";
    }
}
