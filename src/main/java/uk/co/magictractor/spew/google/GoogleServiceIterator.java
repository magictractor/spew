package uk.co.magictractor.spew.google;

import java.util.List;

import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewRequest;
import uk.co.magictractor.spew.api.SpewResponse;
import uk.co.magictractor.spew.api.PageTokenServiceIterator;

public abstract class GoogleServiceIterator<E> extends PageTokenServiceIterator<E> {

    protected GoogleServiceIterator() {
    }

    @Override
    protected final PageAndNextToken<E> fetchPage(String pageToken) {
        SpewRequest request = createPageRequest();

        request.setParam("pageToken", pageToken);

        SpewResponse response = getConnection().request(request);

        List<? extends E> page = parsePageResponse(response);
        String nextToken = response.getString("nextPageToken");

        return new PageAndNextToken<>(page, nextToken);
    }

    protected abstract SpewRequest createPageRequest();

    protected abstract List<? extends E> parsePageResponse(SpewResponse response);

    public static class GoogleServiceIteratorBuilder<E, I extends GoogleServiceIterator<E>, B>
            extends PageServiceIteratorBuilder<E, I, B> {

        protected GoogleServiceIteratorBuilder(SpewConnection connection, I iteratorInstance) {
            super(connection, iteratorInstance);
        }
    }

}
