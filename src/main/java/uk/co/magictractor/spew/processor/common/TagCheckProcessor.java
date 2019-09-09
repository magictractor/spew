package uk.co.magictractor.spew.processor.common;

import java.util.HashSet;
import java.util.Set;

import uk.co.magictractor.spew.photo.Photo;
import uk.co.magictractor.spew.photo.Tag;
import uk.co.magictractor.spew.photo.TagComparator;
import uk.co.magictractor.spew.photo.TagSet;
import uk.co.magictractor.spew.photo.TagType;
import uk.co.magictractor.spew.processor.Processor;

/**
 * Check that tags are known, and that there is a terminal tag (such as "red
 * admiral" rather than just "butterfly").
 */
public class TagCheckProcessor implements Processor<Photo, MutablePhoto, PhotoProcessorContext> {

    private Set<Tag> unknownTags = new HashSet<>();

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
            if (!tagType.isOptional()) {
                System.err.println("No tag of type " + tagType);
            }
        }
        else if (deepestTag.hasChildren()) {
            System.err.println("Deepest tag of type " + tagType + " has more specific child tags: " + deepestTag);
        }
    }

    private void checkNoUnknownTags(TagSet tagSet, PhotoProcessorContext context) {
        for (Tag tag : tagSet.getTags()) {
            if (tag.isUnknown()) {
                unknownTags.add(tag);
            }
        }
    }

    @Override
    public void afterProcessing(PhotoProcessorContext context) {
        System.err.println("afterProcessing");

        if (!unknownTags.isEmpty()) {
            System.err.println("Unknown tags");
            unknownTags.stream().sorted(TagComparator.ASCENDING).map(Tag::getTagName).forEach(System.err::println);
        }
    }
}
