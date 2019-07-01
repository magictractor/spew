package uk.co.magictractor.oauth.api;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.AbstractIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PageServiceIterator<E> extends AbstractIterator<E> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final OAuthConnection connection;
    private List<? extends E> currentPage = Collections.emptyList();
    private int nextPageItemIndex;

    protected PageServiceIterator(OAuthConnection connection) {
        this.connection = connection;
    }

    protected final Logger getLogger() {
        return logger;
    }

    @Override
    public E computeNext() {
        if (nextPageItemIndex >= currentPage.size()) {
            currentPage = nextPage();
            if (currentPage.isEmpty()) {
                // Freshly fetched page is empty - we're done.
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
