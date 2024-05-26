package uk.co.magictractor.spew.api;

import java.util.List;

/**
 * <p>
 * Base class for iterators which fetch items using third party service methods
 * which return a page of items, and service requests are passed a start number
 * and count.
 * </p>
 * <p>
 * The first page is fetched on the first use of {@link #hasNext()} or
 * {@link #next()}, and the next page is fetched only after iterating over all
 * items in the first page.
 * <p>
 */
public abstract class PageCountServiceIterator<E> extends PageServiceIterator<E> {

    private static final int UNKNOWN = -1;

    private int nextPageNumber = 1;

    private int totalPageCount = UNKNOWN;
    private int totalItemCount = UNKNOWN;

    protected PageCountServiceIterator() {
    }

    @Override
    protected List<E> nextPage() {
        /*
         * If known, check the expected number of pages. Some services (such as
         * Flickr getPhotos()) will repeat the last page of items if the page
         * number is too big.
         */
        // TODO! could check total item count too
        if (totalPageCount != UNKNOWN && nextPageNumber > totalPageCount) {
            // TODO! logging
            return endOfPages();
        }
        else {
            List<E> page = fetchPage(nextPageNumber++);
            if (totalPageCount == UNKNOWN && page.isEmpty()) {
                return endOfPages();
            }
            return page;
        }
        // TODO! perhaps endOfPages() if page size is smaller than max?
    }

    // pageNumber is one based in this iterator. If the service uses zero based page
    // numbering, just subtract one from the parameter value.
    protected abstract List<E> fetchPage(int pageNumber);

    public int getTotalPageCount() {
        return totalPageCount;
    }

    protected void setTotalPageCount(int totalPageCount) {
        if (this.totalPageCount == UNKNOWN) {
            this.totalPageCount = totalPageCount;
        }
        else if (this.totalPageCount != totalPageCount) {
            throw new IllegalStateException("totalPageCount should not be changed after it has been set");
        }
    }

    public int getTotalItemCount() {
        return totalItemCount;
    }

    protected void setTotalItemCount(Integer totalItemCount) {
        if (totalItemCount == null) {
            // Note that the total disappeared briefly from Flickr photoset
            // Could perhaps configure the iterator to allow this to be optional.
            throw new IllegalArgumentException("total item count must not be null");
        }
        if (this.totalItemCount == UNKNOWN) {
            this.totalItemCount = totalItemCount;
        }
        else if (this.totalItemCount != totalItemCount) {
            throw new IllegalStateException("totalItemCount should not be changed after it has been set");
        }
    }

    public static class PageCountServiceIteratorBuilder<E, I extends PageCountServiceIterator<E>, B>
            extends PageServiceIteratorBuilder<E, I, B> {

        protected PageCountServiceIteratorBuilder(SpewApplication<?> application, Class<E> elementType,
                I iteratorInstance) {
            super(application, elementType, iteratorInstance);
        }

    }

}
