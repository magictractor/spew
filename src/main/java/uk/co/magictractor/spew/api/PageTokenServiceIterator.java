package uk.co.magictractor.spew.api;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Base class for iterators which fetch items using third party service methods
 * which return a page of items, and service requests are passed a page token
 * for pages after the first page. The first page is fetched on the first use of
 * {@link #hasNext()} or {@link #next()}, and the next page is fetched only
 * after iterating over all items in the first page.>
 * </p>
 * <p>
 * Dropbox uses the term "cursor" for page tokens.
 * </p>
 */
public abstract class PageTokenServiceIterator<E> extends PageServiceIterator<E> {

    private String nextPageToken = null;

    protected PageTokenServiceIterator() {
    }

    @Override
    protected List<? extends E> nextPage() {
        // Get first page, or next page.
        PageAndNextToken<E> pageAndNextToken = fetchPage(nextPageToken);
        nextPageToken = pageAndNextToken.nextToken;
        if (nextPageToken == null) {
            endOfPages();
        }
        return pageAndNextToken.page;
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

    public static class PageTokenServiceIteratorBuilder<E, I extends PageTokenServiceIterator<E>, B>
            extends PageServiceIteratorBuilder<E, I, B> {

        protected PageTokenServiceIteratorBuilder(SpewApplication application, Class<E> elementType,
                I iteratorInstance) {
            super(application, elementType, iteratorInstance);
        }
    }

}
