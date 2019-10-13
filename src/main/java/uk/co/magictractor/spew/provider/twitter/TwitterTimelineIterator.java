package uk.co.magictractor.spew.provider.twitter;

import java.util.List;

import uk.co.magictractor.spew.api.ApplicationRequest;
import uk.co.magictractor.spew.api.PageTokenServiceIterator;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;

public abstract class TwitterTimelineIterator<E> extends PageTokenServiceIterator<E> {

    protected TwitterTimelineIterator() {
    }

    @Override
    protected final PageAndNextToken<E> fetchPage(String pageToken) {
        ApplicationRequest request = createPageRequest();

        getLogger().debug("set max_id={}", pageToken);
        request.setQueryStringParam("max_id", pageToken);

        request.setQueryStringParam("count", getPageSize());

        SpewParsedResponse response = request.sendRequest();

        List<E> page = response.getList("$", getElementType());

        String nextToken;
        if (!page.isEmpty()) {
            long lastTweetId = response.getLong("$.[-1].id");
            nextToken = Long.toString(lastTweetId - 1);
        }
        else {
            nextToken = null;
        }

        return new PageAndNextToken<>(page, nextToken);
    }

    protected abstract ApplicationRequest createPageRequest();

    public static class TwitterTimelineIteratorBuilder<E, I extends TwitterTimelineIterator<E>, B>
            extends PageTokenServiceIteratorBuilder<E, I, B> {

        protected TwitterTimelineIteratorBuilder(SpewApplication<?> application, Class<E> elementType,
                I iteratorInstance) {
            super(application, elementType, iteratorInstance);
            withPageSize(Twitter.MAX_PAGE_SIZE);
        }
    }

}
