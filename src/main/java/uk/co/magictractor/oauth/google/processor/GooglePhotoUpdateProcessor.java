package uk.co.magictractor.oauth.google.processor;

import java.util.Iterator;

import uk.co.magictractor.oauth.api.OAuthConnection;
import uk.co.magictractor.oauth.api.connection.OAuthConnectionFactory;
import uk.co.magictractor.oauth.google.GoogleMediaItemIterator.GoogleMediaItemIteratorBuilder;
import uk.co.magictractor.oauth.google.MyGooglePhotosApp;
import uk.co.magictractor.oauth.google.pojo.GoogleMediaItem;
import uk.co.magictractor.oauth.processor.common.MutablePhoto;
import uk.co.magictractor.oauth.processor.common.PhotoProcessorContext;
import uk.co.magictractor.oauth.processor.common.PhotoTidyProcessorChain;
import uk.co.magictractor.oauth.processor.common.PhotoUpdateProcessor;

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
        OAuthConnection connection = OAuthConnectionFactory.getConnection(MyGooglePhotosApp.class);
        Iterator<GoogleMediaItem> iterator = new GoogleMediaItemIteratorBuilder().withConnection(connection).build();
        processorChain.execute(iterator, new PhotoProcessorContext());
    }

}
