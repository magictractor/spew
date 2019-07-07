package uk.co.magictractor.spew.api;

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

    private SpewConnection connection;

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

    private boolean hasNextPage = true;
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
        // while loop because a Twitter page could (theoretically) be empty
        // but still have subsequent pages to fetch, for example if retweets
        // are excluded and the page contained only retweets
        while (nextPageItemIndex >= currentPage.size()) {
            if (!hasNextPage) {
                // No more data and no more pages - we're done.
                return endOfData();
            }
            currentPage = nextPage();
            nextPageItemIndex = 0;
        }

        return currentPage.get(nextPageItemIndex++);
    }

    /**
     * <p>
     * Note that this could be called from a nextPage() implementation which
     * returns a non-empty List. This may be done if the implementation knows
     * that it's the last page.
     * </p>
     * <p>
     * Like AbstractIterator#endOfData().
     * </p>
     */
    protected List<E> endOfPages() {
        hasNextPage = false;
        return Collections.emptyList();
    }

    protected abstract List<? extends E> nextPage();

    protected SpewConnection getConnection() {
        return connection;
    }

    /**
     * @param <I> iterator type
     * @param <B> builder type
     */
    public static class PageServiceIteratorBuilder<E, I extends PageServiceIterator<E>, B> {

        // Same iterator twice to avoid repeated casting
        private final I iteratorInstance;
        private final PageServiceIterator<E> iter;
        private final Map<Class, Consumer> serverSideFilterHandlers = new HashMap<>();
        private List<Predicate<? super E>> clientSideFilters = null;

        protected PageServiceIteratorBuilder(SpewConnection connection, I iteratorInstance) {
            this.iteratorInstance = iteratorInstance;
            this.iter = iteratorInstance;
            iter.connection = connection;
        }

        protected I getIteratorInstance() {
            return iteratorInstance;
        }

        protected <F> void addServerSideFilterHandler(Class<F> filterType, Consumer<F> filterConsumer) {
            serverSideFilterHandlers.put(filterType, filterConsumer);
        }

        public B withPageSize(int pageSize) {
            iter.pageSize = pageSize;
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
