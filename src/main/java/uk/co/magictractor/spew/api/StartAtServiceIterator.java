package uk.co.magictractor.spew.api;

import java.util.List;

/**
 * Base class for iterators which fetch items using third party service methods
 * which return a page of items, and service requests are passed a page number.
 * The first page is fetched on the first use of {@link #hasNext()} or
 * {@link #next()}, and the next page is fetched only after iterating over all
 * items in the first page.
 */
public abstract class StartAtServiceIterator<E> extends PageServiceIterator<E> {

    private int nextStartAt = 1;

    protected StartAtServiceIterator() {
    }

    @Override
    protected List<E> nextPage() {
        List<E> result = fetchPage(nextStartAt);
        nextStartAt += result.size();
        if (result.size() < getPageSize()) {
            endOfPages();
        }

        return result;
    }

    // startAt is one based in this iterator. If the service uses zero based item
    // numbering, just subtract one from the parameter value.
    protected abstract List<E> fetchPage(int startAt);

    public static class StartAtServiceIteratorBuilder<E, I extends StartAtServiceIterator<E>, B>
            extends PageServiceIteratorBuilder<E, I, B> {

        protected StartAtServiceIteratorBuilder(SpewApplication<?> application, Class<E> elementType,
                I iteratorInstance) {
            super(application, elementType, iteratorInstance);
        }

    }

}
