package uk.co.magictractor.oauth.api;

import java.util.Collections;
import java.util.List;

/**
 * Base class for iterators which fetch items using third party service methods
 * which return a page of items, and service requests are passed a page token
 * for pages after the first page. The first page is fetched on the first use of
 * {@link #hasNext()} or {@link #next()}, and the next page is fetched only
 * after iterating over all items in the first page.
 */
public abstract class PageTokenServiceIterator<E, I extends PageTokenServiceIterator<E, I>>
        extends PageServiceIterator<E, I> {

    // The first page should be fetched without a nextPageToken.
    private boolean first = true;
    private String nextPageToken = null;

    protected PageTokenServiceIterator(OAuthConnection connection) {
        super(connection);
    }

    @Override
    protected List<? extends E> nextPage() {
        if (first || nextPageToken != null) {
            // Get first page, or next page.
            PageAndNextToken<E> pageAndNextToken = fetchPage(nextPageToken);
            first = false;
            nextPageToken = pageAndNextToken.nextToken;
            return pageAndNextToken.page;
        }
        else {
            // Previously fetched page had no nextPageToken, so we're done.
            return Collections.emptyList();
        }
    }

    protected abstract PageAndNextToken<E> fetchPage(String pageToken);

    public static class PageAndNextToken<E> {
        final List<? extends E> page;
        final String nextToken;

        /**
         * @param page
         * @param nextToken may be null to indicate that this is the last page
         */
        public PageAndNextToken(List<? extends E> page, String nextToken) {
            this.page = page == null ? Collections.emptyList() : page;
            this.nextToken = nextToken;
        }
    }

}
