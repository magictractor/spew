package uk.co.magictractor.oauth.flickr.processor;

import java.time.LocalDate;
import java.util.Iterator;

import uk.co.magictractor.oauth.api.OAuthConnection;
import uk.co.magictractor.oauth.api.OAuthRequest;
import uk.co.magictractor.oauth.api.connection.OAuthConnectionFactory;
import uk.co.magictractor.oauth.common.filter.DateTakenPhotoFilter;
import uk.co.magictractor.oauth.flickr.Flickr;
import uk.co.magictractor.oauth.flickr.FlickrPhotoIterator;
import uk.co.magictractor.oauth.flickr.MyFlickrApp;
import uk.co.magictractor.oauth.flickr.pojo.FlickrPhoto;
import uk.co.magictractor.oauth.local.dates.DateRange;
import uk.co.magictractor.oauth.processor.common.MutablePhoto;
import uk.co.magictractor.oauth.processor.common.PhotoProcessorContext;
import uk.co.magictractor.oauth.processor.common.PhotoTidyProcessorChain;
import uk.co.magictractor.oauth.processor.common.PhotoUpdateProcessor;

public class FlickrPhotoUpdateProcessor extends PhotoUpdateProcessor {

    // title and description changed using flickr.photos.setMeta
    // tags using flickr.photos.setTags (also addTags and removeTag)
    // date posted using flickr.photos.setDates
    // @Override
    @Override
    public void process(MutablePhoto photo, PhotoProcessorContext context) {
        if (photo.isTitleChanged()) {
            setMeta(photo);
        }

        if (photo.isTagSetChanged()) {
            setTags(photo);
        }
    }

    //	api_key (Required)
    //	Your API application key. See here for more details.
    //	photo_id (Required)
    //	The id of the photo to set information for.
    //	title (Optional)
    //	The title for the photo. At least one of title or description must be set.
    //	description (Optional)
    //	The description for the photo. At least one of title or description must be set.
    private void setMeta(MutablePhoto photo) {
        // TODO Auto-generated method stub
        System.err.println("set title: " + photo.getTitle());

        OAuthRequest request = OAuthRequest.createPostRequest(Flickr.REST_ENDPOINT);

        request.setParam("method", "flickr.photos.setMeta");

        request.setParam("photo_id", photo.getServiceProviderId());
        request.setParam("title", photo.getTitle());

        OAuthConnectionFactory.getConnection(MyFlickrApp.class).request(request);
    }

    // TODO! move this - it's Flickr specific, perhaps impl for Google Photos too
    private void setTags(MutablePhoto photo) {
        // TODO Auto-generated method stub
        System.err.println("set tags: " + photo.getTagSet());

        OAuthRequest request = OAuthRequest.createPostRequest(Flickr.REST_ENDPOINT);

        request.setParam("method", "flickr.photos.setTags");

        request.setParam("photo_id", photo.getServiceProviderId());
        // TODO! refactor this method - a processor has added the parents already
        request.setParam("tags", photo.getTagSet().getQuotedTagNamesWithParents());

        OAuthConnectionFactory.getConnection(MyFlickrApp.class).request(request);
    }

    // App Garden
    //	{ "photo": {
    //	    "title": { "_content": "API test" },
    //	    "description": { "_content": "" } }, "stat": "ok" }

    // https://api.flickr.com/services/rest/?method=flickr.photos.setMeta&api_key=5939e168bc6ea2e41e83b74f6f0b3e2d&photo_id=45249983521&title=API+test&format=json&nojsoncallback=1&auth_token=72157672277056577-9fa9087d61430e0a&api_sig=50453767437b384152449e7cb561ac02

    public static void main(String[] args) {
        PhotoTidyProcessorChain processorChain = new PhotoTidyProcessorChain(new FlickrPhotoUpdateProcessor());
        OAuthConnection connection = OAuthConnectionFactory.getConnection(MyFlickrApp.class);
        LocalDate since = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        Iterator<FlickrPhoto> iterator = new FlickrPhotoIterator(connection).builder()
                .withFilter(new DateTakenPhotoFilter(DateRange.uptoToday(since)))
                .build();
        processorChain.execute(iterator, new PhotoProcessorContext());
    }

}
