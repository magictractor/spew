package uk.co.magictractor.spew.provider.twitter;

import java.util.List;

import uk.co.magictractor.spew.api.ApplicationRequest;
import uk.co.magictractor.spew.api.PageTokenServiceIterator;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;

// Iterators which return Tweets should extends TweetIterator
public abstract class TwitterCursorIterator<E> extends PageTokenServiceIterator<E> {

    private static final String DONE_CURSOR = "0";

    private final String entitiesKey;

    protected TwitterCursorIterator(String entitiesKey) {
        this.entitiesKey = entitiesKey;
    }

    @Override
    protected final PageAndNextToken<E> fetchPage(String pageToken) {
        ApplicationRequest request = createPageRequest();

        getLogger().debug("set cursor={}", pageToken);
        request.setQueryStringParam("cursor", pageToken);

        request.setQueryStringParam("count", getPageSize());

        SpewParsedResponse response = request.sendRequest();

        List<E> page = response.getList(entitiesKey, getElementType());

        String nextCursor = response.getString("next_cursor_str");
        if (DONE_CURSOR.equals(nextCursor)) {
            nextCursor = null;
        }

        return new PageAndNextToken<>(page, nextCursor);
    }

    protected abstract ApplicationRequest createPageRequest();

    public static class TwitterCursorIteratorBuilder<E, I extends TwitterCursorIterator<E>, B>
            extends PageTokenServiceIteratorBuilder<E, I, B> {

        protected TwitterCursorIteratorBuilder(SpewApplication<?> application, Class<E> elementType,
                I iteratorInstance) {
            super(application, elementType, iteratorInstance);
            withPageSize(Twitter.MAX_PAGE_SIZE);
        }

    }
}
