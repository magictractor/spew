package uk.co.magictractor.spew.example.flickr.processor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.example.flickr.MyFlickrApp;
import uk.co.magictractor.spew.example.flickr.pojo.FlickrPhoto;
import uk.co.magictractor.spew.example.flickr.pojo.FlickrPhotoset;
import uk.co.magictractor.spew.photo.Photo;
import uk.co.magictractor.spew.photo.PhotoComparator;
import uk.co.magictractor.spew.photo.filter.DateTakenPhotoFilter;
import uk.co.magictractor.spew.photo.local.dates.DateRange;
import uk.co.magictractor.spew.processor.common.MutableAlbum;
import uk.co.magictractor.spew.processor.common.MutablePhoto;
import uk.co.magictractor.spew.processor.common.PhotoProcessorContext;
import uk.co.magictractor.spew.processor.common.PhotoTidyProcessorChain;
import uk.co.magictractor.spew.processor.common.PhotoUpdateProcessor;
import uk.co.magictractor.spew.provider.flickr.Flickr;
import uk.co.magictractor.spew.provider.flickr.FlickrPhotoIterator.FlickrPhotoIteratorBuilder;
import uk.co.magictractor.spew.provider.flickr.FlickrPhotosetIterator;
import uk.co.magictractor.spew.provider.flickr.FlickrPhotosetPhotosIterator.FlickrPhotosetPhotosIteratorBuilder;

public class FlickrPhotoUpdateProcessor extends PhotoUpdateProcessor {

    private final SpewApplication application;

    public FlickrPhotoUpdateProcessor(SpewApplication application) {
        this.application = application;
    }

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

        OutgoingHttpRequest request = application.createPostRequest(Flickr.REST_ENDPOINT);

        request.setQueryStringParam("method", "flickr.photos.setMeta");

        request.setQueryStringParam("photo_id", photo.getServiceProviderId());
        request.setQueryStringParam("title", photo.getTitle());

        request.sendRequest();
    }

    private void setTags(MutablePhoto photo) {
        // TODO Auto-generated method stub
        System.err.println("set tags: " + photo.getTagSet());

        OutgoingHttpRequest request = application.createPostRequest(Flickr.REST_ENDPOINT);

        request.setQueryStringParam("method", "flickr.photos.setTags");

        request.setQueryStringParam("photo_id", photo.getServiceProviderId());
        // TODO! refactor this method - a processor has added the parents already
        request.setQueryStringParam("tags", photo.getTagSet().getQuotedTagNamesWithAliasesAndParents());

        request.sendRequest();
    }

    @Override
    public void afterProcessing(PhotoProcessorContext context) {
        syncAlbums(context);
    }

    private void syncAlbums(PhotoProcessorContext context) {
        new FlickrPhotosetIterator.FlickrPhotosetIteratorBuilder<>(application, FlickrPhotoset.class)
                .withFilter(album -> isAlbumOfInterest(album, context))
                .build()
                .forEachRemaining(photoset -> context.getAlbum(photoset.getTitle()).setUnderlyingAlbum(photoset));

        for (MutableAlbum mutableAlbum : context.getAlbums()) {
            if (mutableAlbum.getUnderlyingAlbum() == null) {
                FlickrPhotoset newAlbum = createAlbum(mutableAlbum, context);
                mutableAlbum.setUnderlyingAlbum(newAlbum);
            }
            syncAlbum(mutableAlbum);
        }
    }

    // Ignore albums which are known to the service provider but have not been referenced by any processors.
    private boolean isAlbumOfInterest(FlickrPhotoset album, PhotoProcessorContext context) {
        return context.hasAlbum(album.getTitle());
    }

    /**
     * @param mutableAlbum
     * @param context
     */
    private FlickrPhotoset createAlbum(MutableAlbum mutableAlbum, PhotoProcessorContext context) {
        OutgoingHttpRequest request = application.createPostRequest(Flickr.REST_ENDPOINT);
        request.setQueryStringParam("method", "flickr.photosets.create");
        request.setQueryStringParam("title", mutableAlbum.getTitle());
        request.setQueryStringParam("primary_photo_id", mutableAlbum.getPhotos().get(0).getServiceProviderId());

        SpewParsedResponse response = request.sendRequest();
        System.err.println(response);

        return response.getObject("$.photoset", FlickrPhotoset.class);
    }

    private void syncAlbum(MutableAlbum album) {
        List<String> existingPhotoIds = fetchExistingPhotoIds(album);
        System.err.println("existing photo ids: " + existingPhotoIds);

        List<String> targetPhotoIds = album.getPhotos()
                .stream()
                .sorted(PhotoComparator.DATE_TIME_ASC)
                .map(Photo::getServiceProviderId)
                .collect(Collectors.toList());

        List<String> photoIdsToBeAdded = new ArrayList<>(targetPhotoIds);
        photoIdsToBeAdded.removeAll(existingPhotoIds);
        System.err.println("to be added: " + photoIdsToBeAdded);
        if (!photoIdsToBeAdded.isEmpty()) {
            addPhotos(album, photoIdsToBeAdded);
        }

        List<String> photoIdsToBeRemoved = new ArrayList<>(existingPhotoIds);
        photoIdsToBeRemoved.removeAll(targetPhotoIds);
        System.err.println("to be removed: " + photoIdsToBeRemoved);
        if (!photoIdsToBeRemoved.isEmpty()) {
            removePhotos(album, photoIdsToBeRemoved);
        }

        if (!existingPhotoIds.equals(targetPhotoIds)) {
            System.err.println("reorder required");
            reorderPhotos(album, targetPhotoIds);
        }
        else {
            System.err.println("order correct");
        }
    }

    private List<String> fetchExistingPhotoIds(MutableAlbum album) {
        return new FlickrPhotosetPhotosIteratorBuilder<FlickrPhoto>(application, FlickrPhoto.class,
            album.getServiceProviderId())
                    .buildStream()
                    .map(Photo::getServiceProviderId)
                    .collect(Collectors.toList());
    }

    private void addPhotos(MutableAlbum album, List<String> photoIds) {
        String photosetId = album.getServiceProviderId();
        for (String photoId : photoIds) {
            addPhoto(photosetId, photoId);
        }
    }

    private void addPhoto(String photosetId, String photoId) {
        OutgoingHttpRequest request = application.createPostRequest(Flickr.REST_ENDPOINT);
        request.setQueryStringParam("method", "flickr.photosets.addPhoto");
        request.setQueryStringParam("photoset_id", photosetId);
        request.setQueryStringParam("photo_id", photoId);

        request.sendRequest();
    }

    private void removePhotos(MutableAlbum album, List<String> photoIdsToBeRemoved) {
        throw new UnsupportedOperationException("removePhotos() has not yet been implemented");
    }

    private void reorderPhotos(MutableAlbum album, List<String> photoIds) {
        OutgoingHttpRequest request = application.createPostRequest(Flickr.REST_ENDPOINT);
        request.setQueryStringParam("method", "flickr.photosets.reorderPhotos");
        request.setQueryStringParam("photoset_id", album.getServiceProviderId());
        String photoIdsString = Joiner.on(',').join(photoIds);
        request.setQueryStringParam("photo_ids", photoIdsString);

        request.sendRequest();
    }

    // App Garden
    //	{ "photo": {
    //	    "title": { "_content": "API test" },
    //	    "description": { "_content": "" } }, "stat": "ok" }

    // https://api.flickr.com/services/rest/?method=flickr.photos.setMeta&api_key=5939e168bc6ea2e41e83b74f6f0b3e2d&photo_id=45249983521&title=API+test&format=json&nojsoncallback=1&auth_token=72157672277056577-9fa9087d61430e0a&api_sig=50453767437b384152449e7cb561ac02

    public static void main(String[] args) {
        PhotoTidyProcessorChain processorChain = new PhotoTidyProcessorChain(
            new FlickrPhotoUpdateProcessor(new MyFlickrApp()));
        LocalDate since = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        Iterator<FlickrPhoto> iterator = new FlickrPhotoIteratorBuilder<>(new MyFlickrApp(), FlickrPhoto.class)
                .withFilter(new DateTakenPhotoFilter(DateRange.uptoToday(since)))
                .build();
        processorChain.execute(iterator, new PhotoProcessorContext());
    }

}
