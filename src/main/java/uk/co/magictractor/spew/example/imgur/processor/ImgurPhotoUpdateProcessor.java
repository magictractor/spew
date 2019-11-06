package uk.co.magictractor.spew.example.imgur.processor;

import java.util.Iterator;

import uk.co.magictractor.spew.example.imgur.MyImgurApp;
import uk.co.magictractor.spew.example.imgur.pojo.ImgurImage;
import uk.co.magictractor.spew.processor.common.MutableMedia;
import uk.co.magictractor.spew.processor.common.MediaProcessorContext;
import uk.co.magictractor.spew.processor.common.PhotoTidyProcessorChain;
import uk.co.magictractor.spew.processor.common.MediaUpdateProcessor;
import uk.co.magictractor.spew.provider.imgur.ImgurPhotoIterator.ImgurPhotoIteratorBuilder;

// TODO! currently just a stub
public class ImgurPhotoUpdateProcessor extends MediaUpdateProcessor {

    @Override
    public void process(MutableMedia photo, MediaProcessorContext context) {
        //		if (photo.isTitleChanged()) {
        //			setMeta(photo);
        //		}
        //
        //		if (photo.isTagSetChanged()) {
        //			setTags(photo);
        //		}
        System.err.println("processing not done: " + photo);
    }

    public static void main(String[] args) {
        PhotoTidyProcessorChain processorChain = new PhotoTidyProcessorChain(new ImgurPhotoUpdateProcessor());
        Iterator<ImgurImage> iterator = new ImgurPhotoIteratorBuilder<>(MyImgurApp.get(), ImgurImage.class).build();
        processorChain.execute(iterator, new MediaProcessorContext());
    }

}
