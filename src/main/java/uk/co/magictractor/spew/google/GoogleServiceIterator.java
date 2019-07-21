package uk.co.magictractor.spew.google;

import java.util.List;

import uk.co.magictractor.spew.api.PageTokenServiceIterator;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewRequest;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;

public abstract class GoogleServiceIterator<E> extends PageTokenServiceIterator<E> {

    protected GoogleServiceIterator() {
    }

    @Override
    protected final PageAndNextToken<E> fetchPage(String pageToken) {
        SpewRequest request = createPageRequest();

        request.setQueryStringParam("pageToken", pageToken);

        SpewParsedResponse response = request.sendRequest();

        List<? extends E> page = parsePageResponse(response);
        String nextToken = response.getString("nextPageToken");

        return new PageAndNextToken<>(page, nextToken);
    }

    protected abstract SpewRequest createPageRequest();

    protected abstract List<? extends E> parsePageResponse(SpewParsedResponse response);

    public static class GoogleServiceIteratorBuilder<E, I extends GoogleServiceIterator<E>, B>
            extends PageServiceIteratorBuilder<E, I, B> {

        protected GoogleServiceIteratorBuilder(SpewApplication application, Class<E> elementType, I iteratorInstance) {
            super(application, elementType, iteratorInstance);
        }
    }

}
