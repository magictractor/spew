package uk.co.magictractor.oauth.processor.local;

import uk.co.magictractor.oauth.common.Photo;
import uk.co.magictractor.oauth.processor.Processor;
import uk.co.magictractor.oauth.processor.SimpleProcessorContext;

/**
 * Read all photo tags and compile a list of subjects seen in The Botanics. An
 * appropriate iterator is expected to be used with the processor chain to
 * restrict processed photos to a single month.
 */
public class BotanicsReportProcessor implements Processor<Photo, Photo, SimpleProcessorContext<Photo>> {

    @Override
    public void process(Photo photo, SimpleProcessorContext<Photo> context) {
        System.err.println("photo: " + photo.getFileName());
        photo.getTagSet();
    }

}
