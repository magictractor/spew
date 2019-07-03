package uk.co.magictractor.oauth.google;

import java.util.List;

import uk.co.magictractor.oauth.api.OAuthConnection;
import uk.co.magictractor.oauth.api.OAuthRequest;
import uk.co.magictractor.oauth.api.OAuthResponse;
import uk.co.magictractor.oauth.api.PageTokenServiceIterator;

public abstract class GoogleServiceIterator<E> extends PageTokenServiceIterator<E> {

    protected GoogleServiceIterator() {
    }

    @Override
    protected final PageAndNextToken<E> fetchPage(String pageToken) {
        OAuthRequest request = createPageRequest();

        request.setParam("pageToken", pageToken);

        OAuthResponse response = getConnection().request(request);

        List<? extends E> page = parsePageResponse(response);
        String nextToken = response.getString("nextPageToken");

        return new PageAndNextToken<>(page, nextToken);
    }

    protected abstract OAuthRequest createPageRequest();

    protected abstract List<? extends E> parsePageResponse(OAuthResponse response);

    public static class GoogleServiceIteratorBuilder<E, I extends GoogleServiceIterator<E>, B>
            extends PageServiceIteratorBuilder<E, I, B> {

        protected GoogleServiceIteratorBuilder(OAuthConnection connection, I iteratorInstance) {
            super(connection, iteratorInstance);
        }
    }

}
