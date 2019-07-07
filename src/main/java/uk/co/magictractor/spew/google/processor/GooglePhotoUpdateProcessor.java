package uk.co.magictractor.spew.google.processor;

import java.util.Iterator;

import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.connection.OAuthConnectionFactory;
import uk.co.magictractor.spew.google.MyGooglePhotosApp;
import uk.co.magictractor.spew.google.GoogleMediaItemIterator.GoogleMediaItemIteratorBuilder;
import uk.co.magictractor.spew.google.pojo.GoogleMediaItem;
import uk.co.magictractor.spew.processor.common.MutablePhoto;
import uk.co.magictractor.spew.processor.common.PhotoProcessorContext;
import uk.co.magictractor.spew.processor.common.PhotoTidyProcessorChain;
import uk.co.magictractor.spew.processor.common.PhotoUpdateProcessor;

// TODO! currently just a stub
public class GooglePhotoUpdateProcessor extends PhotoUpdateProcessor {

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
        PhotoTidyProcessorChain processorChain = new PhotoTidyProcessorChain(new GooglePhotoUpdateProcessor());
        SpewConnection connection = OAuthConnectionFactory.getConnection(MyGooglePhotosApp.class);
        Iterator<GoogleMediaItem> iterator = new GoogleMediaItemIteratorBuilder(connection).build();
        processorChain.execute(iterator, new PhotoProcessorContext());
    }

}
