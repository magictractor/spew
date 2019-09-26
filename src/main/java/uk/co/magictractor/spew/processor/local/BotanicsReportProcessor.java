package uk.co.magictractor.spew.processor.local;

import uk.co.magictractor.spew.photo.Media;
import uk.co.magictractor.spew.processor.Processor;
import uk.co.magictractor.spew.processor.SimpleProcessorContext;

/**
 * Read all photo tags and compile a list of subjects seen in The Botanics. An
 * appropriate iterator is expected to be used with the processor chain to
 * restrict processed photos to a single month.
 */
public class BotanicsReportProcessor implements Processor<Media, Media, SimpleProcessorContext<Media>> {

    @Override
    public void process(Media image, SimpleProcessorContext<Media> context) {
        System.err.println("image: " + image.getFileName());
        image.getTagSet();
    }

}
