package uk.co.magictractor.spew.processor.common;

import uk.co.magictractor.spew.photo.Tag;
import uk.co.magictractor.spew.processor.MediaProcessor;

/**
 * Ensure that parent tags are included too. For example for "common hawker"
 * ensure "dragonfly" and "odonata" tags are included.
 */
public class TagFixProcessor implements MediaProcessor {

    private final Tag wrongTag;
    private final Tag rightTag;

    public TagFixProcessor(String wrongTag, String rightTag) {
        this.wrongTag = Tag.fetchOrCreateTag(wrongTag);
        this.rightTag = rightTag == null ? null : Tag.fetchTag(rightTag);
    }

    @Override
    public void process(MutableMedia photoChanges, MediaProcessorContext context) {
        if (photoChanges.getTagSet().getTags().contains(wrongTag)) {
            photoChanges.getTagSet().removeTag(wrongTag);
            if (rightTag != null) {
                photoChanges.getTagSet().addTag(rightTag);
            }
        }
    }

}
