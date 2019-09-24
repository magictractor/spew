package uk.co.magictractor.spew.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PageServiceIterator<E> extends AbstractIterator<E> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private SpewApplication<?> application;
    private List<Consumer<OutgoingHttpRequest>> beforeSendConsumers;
    private Class<E> elementType;

    /**
     * <p>
     * The page size is a maximum. For probably all iterators the last page is
     * likely to contain fewer elements than this. For some iterators (such as
     * Twitter) this serves as a maximum.
     * </p>
     * <p>
     * If null (the default) use the service provider's default page size.
     * </p>
     */
    private Integer pageSize;

    private boolean hasNextPage = true;
    private List<? extends E> currentPage = Collections.emptyList();
    private int nextPageItemIndex;

    protected PageServiceIterator() {
    }

    protected Class<E> getElementType() {
        return elementType;
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

    protected OutgoingHttpRequest createGetRequest(String url) {
        return createRequest("GET", url);
    }

    protected OutgoingHttpRequest createPostRequest(String url) {
        return createRequest("POST", url);
    }

    protected OutgoingHttpRequest createRequest(String httpMethod, String url) {
        OutgoingHttpRequest request = application.createRequest(httpMethod, url);
        request.beforeSend(r -> beforeSend(r));
        return request;
    }

    private void beforeSend(OutgoingHttpRequest request) {
        if (beforeSendConsumers != null) {
            beforeSendConsumers.forEach(rc -> rc.accept(request));
        }
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

        protected PageServiceIteratorBuilder(SpewApplication<?> application, Class<E> elementType, I iteratorInstance) {
            this.iteratorInstance = iteratorInstance;
            this.iter = iteratorInstance;
            iter.application = application;
            iter.elementType = elementType;
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

        /**
         * <p>
         * A hook which permits body and query parameter values to be set on
         * requests from the iterator which are not set by the iterator, or to
         * override values set by the iterator.
         * </p>
         * <p>
         * This allows iterator implementations to be minimal while allowing
         * implementors flexibility to use all options provided by the API.
         * </p>
         */
        public B withBeforeSendConsumer(Consumer<OutgoingHttpRequest> beforeSendConsumer) {
            if (iter.beforeSendConsumers == null) {
                iter.beforeSendConsumers = new ArrayList<>();
            }
            iter.beforeSendConsumers.add(beforeSendConsumer);
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

        @SuppressWarnings("unchecked")
        public <T extends E> Iterator<T> buildForSubType(Class<E> subType) {
            return (Iterator<T>) Iterators.filter(build(), (e) -> subType.isInstance(e));
        }

        public List<E> buildList() {
            return Lists.newArrayList(build());
        }

        public Stream<E> buildStream() {
            return Streams.stream(build());
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
