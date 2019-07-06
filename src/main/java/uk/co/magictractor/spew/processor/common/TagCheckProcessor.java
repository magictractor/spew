package uk.co.magictractor.spew.processor.common;

import uk.co.magictractor.spew.common.Photo;
import uk.co.magictractor.spew.common.Tag;
import uk.co.magictractor.spew.common.TagSet;
import uk.co.magictractor.spew.common.TagType;
import uk.co.magictractor.spew.processor.Processor;

/**
 * Check that tags are known, and that there is a terminal tag (such as "red
 * admiral" rather than just "butterfly").
 */
public class TagCheckProcessor implements Processor<Photo, MutablePhoto, PhotoProcessorContext> {

    @Override
    public void process(MutablePhoto photo, PhotoProcessorContext context) {
        TagSet tagSet = photo.getTagSet();
        if (tagSet == null) {
            return;
        }

        for (TagType tagType : TagType.values()) {
            checkTagType(tagType, tagSet, context);
        }
        checkNoUnknownTags(tagSet, context);
    }

    private void checkTagType(TagType tagType, TagSet tagSet, PhotoProcessorContext context) {
        Tag deepestTag = tagSet.getDeepestTag(tagType);
        if (deepestTag == null) {
            System.err.println("No tag of type " + tagType);
        }
        else if (deepestTag.hasChildren()) {
            System.err.println("Deepest tag of type " + tagType + " has more specific child tags: " + deepestTag);
        }
    }

    private void checkNoUnknownTags(TagSet tagSet, PhotoProcessorContext context) {
        for (Tag tag : tagSet.getTags()) {
            if (tag.isUnknown()) {
                context.addUnknownTag(tag);
            }
        }
    }
}
