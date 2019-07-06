package uk.co.magictractor.spew.processor.common;

import uk.co.magictractor.spew.common.Photo;
import uk.co.magictractor.spew.common.Tag;
import uk.co.magictractor.spew.common.TagSet;
import uk.co.magictractor.spew.common.TagType;
import uk.co.magictractor.spew.processor.Processor;

/**
 * Ensure that parent tags are included too. For example for "common hawker"
 * ensure "dragonfly" and "odonata" tags are included.
 */
public class TagHierarchyProcessor implements Processor<Photo, MutablePhoto, PhotoProcessorContext> {

    @Override
    public void process(MutablePhoto photoChanges, PhotoProcessorContext context) {
        TagSet tagSet = photoChanges.getTagSet();
        if (tagSet == null) {
            return;
        }

        for (TagType tagType : TagType.values()) {
            addTagHierarchy(tagType, tagSet);
        }
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