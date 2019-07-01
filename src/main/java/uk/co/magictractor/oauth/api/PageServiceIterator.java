package uk.co.magictractor.oauth.api;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.AbstractIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PageServiceIterator<E, I extends PageServiceIterator<E, I>> extends AbstractIterator<E> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final OAuthConnection connection;
    private List<? extends E> currentPage = Collections.emptyList();
    private int nextPageItemIndex;
    /**
     * <p>
     * The page size is a maximum. For probably all iterators the last page is
     * likely to contain fewer elements than this. For some iterators (such as
     * Twitter) this serves as a maximum.
     * </p>
     * <p>
     * If null (the default) use the service provider's default page size.
     * </p>
     * *
     */
    private Integer pageSize;

    protected PageServiceIterator(OAuthConnection connection) {
        this.connection = connection;
    }

    protected final Logger getLogger() {
        return logger;
    }

    @SuppressWarnings("unchecked")
    public I withPageSize(int pageSize) {
        this.pageSize = pageSize;
        return (I) this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    @Override
    public E computeNext() {
        if (nextPageItemIndex >= currentPage.size()) {
            currentPage = nextPage();
            if (currentPage.isEmpty()) {
                // Freshly fetched page is empty - we're done.
                // TODO! not necessarily true with small pages from Twitter?
                return endOfData();
            }
            nextPageItemIndex = 0;
        }

        return currentPage.get(nextPageItemIndex++);
    }

    protected abstract List<? extends E> nextPage();

    protected OAuthConnection getConnection() {
        return connection;
    }

}
