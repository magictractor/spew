package uk.co.magictractor.spew.example.imgur.processor;

import java.util.Iterator;

import uk.co.magictractor.spew.example.imgur.MyImgurApp;
import uk.co.magictractor.spew.example.imgur.pojo.ImgurImage;
import uk.co.magictractor.spew.processor.common.MutablePhoto;
import uk.co.magictractor.spew.processor.common.PhotoProcessorContext;
import uk.co.magictractor.spew.processor.common.PhotoTidyProcessorChain;
import uk.co.magictractor.spew.processor.common.PhotoUpdateProcessor;
import uk.co.magictractor.spew.provider.imgur.ImgurPhotoIterator.ImgurPhotoIteratorBuilder;

// TODO! currently just a stub
public class ImgurPhotoUpdateProcessor extends PhotoUpdateProcessor {

    @Override
    public void process(MutablePhoto photo, PhotoProcessorContext context) {
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
        Iterator<ImgurImage> iterator = new ImgurPhotoIteratorBuilder<>(new MyImgurApp(), ImgurImage.class).build();
        processorChain.execute(iterator, new PhotoProcessorContext());
    }

}
