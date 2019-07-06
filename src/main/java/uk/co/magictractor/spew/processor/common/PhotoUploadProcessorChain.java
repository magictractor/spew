package uk.co.magictractor.spew.processor.common;

import uk.co.magictractor.spew.common.Photo;
import uk.co.magictractor.spew.processor.ProcessorChain;

/** Upload new photos in the local collection to a service provider. */
public class PhotoUploadProcessorChain extends ProcessorChain<Photo, MutablePhoto, PhotoProcessorContext> {

    public PhotoUploadProcessorChain(PhotoUploadProcessor uploadProcessor) {

        addProcessor(uploadProcessor);
    }

}