package uk.co.magictractor.oauth.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PageServiceIterator<E> extends AbstractIterator<E> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /* default */ OAuthConnection connection;

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
    /* default */ Integer pageSize;

    private List<? extends E> currentPage = Collections.emptyList();
    private int nextPageItemIndex;

    protected PageServiceIterator() {
    }

    protected final Logger getLogger() {
        return logger;
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

    /**
     * @param <I> iterator type
     * @param <B> builder type
     */
    public static class PageServiceIteratorBuilder<E, I extends PageServiceIterator<E>, B> {

        private final I iteratorInstance;
        private final Map<Class, Consumer> serverSideFilterHandlers = new HashMap<>();
        private List<Predicate<? super E>> clientSideFilters = null;

        protected PageServiceIteratorBuilder(I iteratorInstance) {
            this.iteratorInstance = iteratorInstance;
        }

        protected I getIteratorInstance() {
            return iteratorInstance;
        }

        protected <F> void addServerSideFilterHandler(Class<F> filterType, Consumer<F> filterConsumer) {
            serverSideFilterHandlers.put(filterType, filterConsumer);
        }

        public B withConnection(OAuthConnection connection) {
            iteratorInstance.connection = connection;
            return (B) this;
        }

        public B withPageSize(int pageSize) {
            iteratorInstance.pageSize = pageSize;
            return (B) this;
        }

        public B withFilter(Predicate<? super E> filter) {
            @SuppressWarnings("unchecked")
            Consumer<Predicate<? super E>> serverSideFilterHandler = serverSideFilterHandlers
                    .get(filter.getClass());
            if (serverSideFilterHandler != null) {
                // The filter is used to modify the request so that only desired values
                // are returned from the service provider.
                serverSideFilterHandler.accept(filter);
            }
            else {
                // The filter is applied on the client side, so many value could be
                // returned from the service provider which are then ignored.
                if (clientSideFilters == null) {
                    clientSideFilters = new ArrayList<>();
                }
                clientSideFilters.add(filter);
            }
            return (B) this;
        }

        public Iterator<E> build() {
            if (clientSideFilters == null) {
                return iteratorInstance;
            }
            // generics not quite right?
            //            else if (clientSideFilters.size() == 1) {
            //                return Iterators.filter(iteratorInstance, clientSideFilters.get(0));
            //            }
            else {
                return Iterators.filter(iteratorInstance, this::clientSideFilter);
            }
        }

        private boolean clientSideFilter(E element) {
            for (Predicate<? super E> clientSideFilter : clientSideFilters) {
                if (!clientSideFilter.test(element)) {
                    return false;
                }
            }
            return true;
        }
    }

}
