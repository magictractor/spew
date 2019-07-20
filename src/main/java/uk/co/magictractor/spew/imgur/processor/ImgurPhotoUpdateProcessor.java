package uk.co.magictractor.spew.imgur.processor;

import java.util.Iterator;

import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.connection.SpewConnectionFactory;
import uk.co.magictractor.spew.imgur.ImgurPhotoIterator.ImgurPhotoIteratorBuilder;
import uk.co.magictractor.spew.imgur.MyImgurApp;
import uk.co.magictractor.spew.imgur.pojo.ImgurImage;
import uk.co.magictractor.spew.processor.common.MutablePhoto;
import uk.co.magictractor.spew.processor.common.PhotoProcessorContext;
import uk.co.magictractor.spew.processor.common.PhotoTidyProcessorChain;
import uk.co.magictractor.spew.processor.common.PhotoUpdateProcessor;

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
        SpewConnection connection = SpewConnectionFactory.getConnection(MyImgurApp.class);
        Iterator<ImgurImage> iterator = new ImgurPhotoIteratorBuilder<>(connection, ImgurImage.class).build();
        processorChain.execute(iterator, new PhotoProcessorContext());
    }

}
