package uk.co.magictractor.spew.google;

import java.util.Iterator;
import java.util.List;

import com.jayway.jsonpath.TypeRef;

import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewRequest;
import uk.co.magictractor.spew.api.SpewResponse;
import uk.co.magictractor.spew.api.connection.OAuthConnectionFactory;
import uk.co.magictractor.spew.common.Album;
import uk.co.magictractor.spew.google.pojo.GoogleAlbum;

public class GoogleSharedAlbumsIterator extends GoogleServiceIterator<GoogleAlbum> {

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
    protected List<GoogleAlbum> parsePageResponse(SpewResponse response) {
        return response.getObject("sharedAlbums", new TypeRef<List<GoogleAlbum>>() {
        });
    }

    public static class GoogleSharedAlbumsIteratorBuilder extends
            GoogleServiceIteratorBuilder<GoogleAlbum, GoogleSharedAlbumsIterator, GoogleSharedAlbumsIteratorBuilder> {

        public GoogleSharedAlbumsIteratorBuilder(SpewConnection connection) {
            super(connection, new GoogleSharedAlbumsIterator());
        }

    }

    // https://developers.google.com/photos/library/reference/rest/v1/albums#Album
    public static void main(String[] args) {
        SpewConnection connection = OAuthConnectionFactory.getConnection(MyGooglePhotosApp.class);
        Iterator<GoogleAlbum> iter = new GoogleSharedAlbumsIteratorBuilder(connection).build();
        while (iter.hasNext()) {
            Album album = iter.next();
            System.err.println(
                album.getTitle() + " " + album.getId() + " " + album.getAlbumUrl() + " " + album.getPhotoCount());
        }
    }
}
