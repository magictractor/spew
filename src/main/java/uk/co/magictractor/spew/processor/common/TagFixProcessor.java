package uk.co.magictractor.spew.processor.common;

import uk.co.magictractor.spew.photo.Media;
import uk.co.magictractor.spew.photo.Tag;
import uk.co.magictractor.spew.processor.Processor;

/**
 * Ensure that parent tags are included too. For example for "common hawker"
 * ensure "dragonfly" and "odonata" tags are included.
 */
public class TagFixProcessor implements Processor<Media, MutablePhoto, PhotoProcessorContext> {

    private final Tag wrongTag;
    private final Tag rightTag;

    public TagFixProcessor(String wrongTag, String rightTag) {
        this.wrongTag = Tag.fetchOrCreateTag(wrongTag);
        this.rightTag = rightTag == null ? null : Tag.fetchTag(rightTag);
    }

    @Override
    public void process(MutablePhoto photoChanges, PhotoProcessorContext context) {
        if (photoChanges.getTagSet().getTags().contains(wrongTag)) {
            photoChanges.getTagSet().removeTag(wrongTag);
            if (rightTag != null) {
                photoChanges.getTagSet().addTag(rightTag);
            }
        }
    }

}
