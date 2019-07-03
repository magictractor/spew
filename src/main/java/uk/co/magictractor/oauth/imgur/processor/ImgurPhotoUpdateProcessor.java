package uk.co.magictractor.oauth.imgur.processor;

import java.util.Iterator;

import uk.co.magictractor.oauth.api.OAuthConnection;
import uk.co.magictractor.oauth.api.connection.OAuthConnectionFactory;
import uk.co.magictractor.oauth.imgur.ImgurPhotoIterator.ImgurPhotoIteratorBuilder;
import uk.co.magictractor.oauth.imgur.MyImgurApp;
import uk.co.magictractor.oauth.imgur.pojo.ImgurImage;
import uk.co.magictractor.oauth.processor.common.MutablePhoto;
import uk.co.magictractor.oauth.processor.common.PhotoProcessorContext;
import uk.co.magictractor.oauth.processor.common.PhotoTidyProcessorChain;
import uk.co.magictractor.oauth.processor.common.PhotoUpdateProcessor;

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
        OAuthConnection connection = OAuthConnectionFactory.getConnection(MyImgurApp.class);
        Iterator<ImgurImage> iterator = new ImgurPhotoIteratorBuilder(connection).build();
        processorChain.execute(iterator, new PhotoProcessorContext());
    }

}
