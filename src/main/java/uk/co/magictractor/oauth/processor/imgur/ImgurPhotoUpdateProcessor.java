package uk.co.magictractor.oauth.processor.imgur;

import uk.co.magictractor.oauth.imgur.ImgurPhotoIterator;
import uk.co.magictractor.oauth.imgur.MyImgurApp;
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
        processorChain.execute(new ImgurPhotoIterator(MyImgurApp.getInstance()), new PhotoProcessorContext());
    }

}
