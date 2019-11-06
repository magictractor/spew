package uk.co.magictractor.spew.processor.common;

import uk.co.magictractor.spew.photo.Tag;
import uk.co.magictractor.spew.photo.TagComparator;
import uk.co.magictractor.spew.photo.TagSet;
import uk.co.magictractor.spew.photo.TagType;
import uk.co.magictractor.spew.processor.MediaProcessor;

/**
 * <p>
 * Ensure that parent tags are included too. For example for "common hawker"
 * ensure "dragonfly" and "odonata" tags are included.
 * </p>
 * <p>
 * Also ensure tags are correctly ordered.
 * </p>
 */
public class TagHierarchyProcessor implements MediaProcessor {

    @Override
    public void process(MutableMedia photoChanges, MediaProcessorContext context) {
        TagSet tagSet = photoChanges.getTagSet();
        if (tagSet == null) {
            return;
        }

        for (TagType tagType : TagType.values()) {
            addTagHierarchy(tagType, tagSet);
        }

        tagSet.sort(TagComparator.ASCENDING);
    }

    private void addTagHierarchy(TagType tagType, TagSet tagSet) {
        Tag deepestTag = tagSet.getDeepestTag(tagType);

        if (deepestTag == null) {
            // missing tag will be reported by TagCheckProcessor
            return;
        }

        Tag parentTag = deepestTag.getParent();
        while (parentTag != null) {
            tagSet.addTag(parentTag);
            parentTag = parentTag.getParent();
        }
    }

}
