package uk.co.magictractor.spew.processor.local;

import uk.co.magictractor.spew.photo.Image;
import uk.co.magictractor.spew.processor.Processor;
import uk.co.magictractor.spew.processor.SimpleProcessorContext;

/**
 * Read all photo tags and compile a list of subjects seen in The Botanics. An
 * appropriate iterator is expected to be used with the processor chain to
 * restrict processed photos to a single month.
 */
public class BotanicsReportProcessor implements Processor<Image, Image, SimpleProcessorContext<Image>> {

    @Override
    public void process(Image image, SimpleProcessorContext<Image> context) {
        System.err.println("image: " + image.getFileName());
        image.getTagSet();
    }

}
