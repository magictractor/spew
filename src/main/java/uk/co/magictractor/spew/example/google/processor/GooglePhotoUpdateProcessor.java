package uk.co.magictractor.spew.example.google.processor;

import java.util.Iterator;

import uk.co.magictractor.spew.example.google.MyGooglePhotosApp;
import uk.co.magictractor.spew.example.google.pojo.GoogleImage;
import uk.co.magictractor.spew.processor.common.MutableMedia;
import uk.co.magictractor.spew.processor.common.MediaProcessorContext;
import uk.co.magictractor.spew.processor.common.PhotoTidyProcessorChain;
import uk.co.magictractor.spew.processor.common.MediaUpdateProcessor;
import uk.co.magictractor.spew.provider.google.GoogleMediaItemIterator.GoogleMediaItemIteratorBuilder;

// TODO! currently just a stub
public class GooglePhotoUpdateProcessor extends MediaUpdateProcessor {

    @Override
    public void process(MutableMedia photo, MediaProcessorContext context) {
        //      if (photo.isTitleChanged()) {
        //          setMeta(photo);
        //      }
        //
        //      if (photo.isTagSetChanged()) {
        //          setTags(photo);
        //      }
        System.err.println("processing not done: " + photo);
    }

    public static void main(String[] args) {
        PhotoTidyProcessorChain processorChain = new PhotoTidyProcessorChain(new GooglePhotoUpdateProcessor());
        Iterator<GoogleImage> iterator = new GoogleMediaItemIteratorBuilder<>(MyGooglePhotosApp.get(),
            GoogleImage.class)
                    .build();
        processorChain.execute(iterator, new MediaProcessorContext());
    }

}
