package uk.co.magictractor.oauth.api;

import uk.co.magictractor.oauth.common.Photo;

public abstract class PageServicePhotoIterator<P extends Photo> extends PageServiceIterator<P> {

    protected PageServicePhotoIterator(OAuthConnection connection) {
        super(connection);
    }

}
