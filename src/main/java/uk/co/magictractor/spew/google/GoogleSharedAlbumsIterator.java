package uk.co.magictractor.spew.google;

import java.util.Iterator;
import java.util.List;

import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewRequest;
import uk.co.magictractor.spew.api.SpewResponse;
import uk.co.magictractor.spew.api.connection.SpewConnectionFactory;
import uk.co.magictractor.spew.common.Album;
import uk.co.magictractor.spew.google.pojo.GoogleAlbum;

public class GoogleSharedAlbumsIterator<E> extends GoogleServiceIterator<E> {

    private GoogleSharedAlbumsIterator() {
    }

    @Override
    protected SpewRequest createPageRequest() {
        // This would include unshared albums
        // https://developers.google.com/photos/library/reference/rest/v1/albums/list
        // return new OAuthRequest("https://photoslibrary.googleapis.com/v1/albums");

        // https://developers.google.com/photos/library/reference/rest/v1/sharedAlbums/list
        return SpewRequest.createGetRequest("https://photoslibrary.googleapis.com/v1/sharedAlbums");
    }

    @Override
    protected List<E> parsePageResponse(SpewResponse response) {
        return response.getList("sharedAlbums", getElementType());
    }

    public static class GoogleSharedAlbumsIteratorBuilder<E> extends
            GoogleServiceIteratorBuilder<E, GoogleSharedAlbumsIterator<E>, GoogleSharedAlbumsIteratorBuilder<E>> {

        public GoogleSharedAlbumsIteratorBuilder(SpewConnection connection, Class<E> elementType) {
            super(connection, elementType, new GoogleSharedAlbumsIterator<>());
        }

    }

    // https://developers.google.com/photos/library/reference/rest/v1/albums#Album
    public static void main(String[] args) {
        SpewConnection connection = SpewConnectionFactory.getConnection(MyGooglePhotosApp.class);
        Iterator<GoogleAlbum> iter = new GoogleSharedAlbumsIteratorBuilder<>(connection, GoogleAlbum.class).build();
        while (iter.hasNext()) {
            Album album = iter.next();
            System.err.println(
                album.getTitle() + " " + album.getServiceProviderId() + " " + album.getAlbumUrl() + " " + album.getPhotoCount());
        }
    }
}
