package uk.co.magictractor.spew.api;

import java.util.List;

/**
 * Base class for iterators which return multiple elements but provide no
 * mechanism for paging results.
 */
public abstract class SingleCallServiceIterator<E> extends PageServiceIterator<E> {

    protected SingleCallServiceIterator() {
    }

    @Override
    protected List<E> nextPage() {
        List<E> page = fetchPage();
        endOfPages();
        return page;
    }

    protected abstract List<E> fetchPage();

    public static class SingleCallServiceIteratorBuilder<E, I extends SingleCallServiceIterator<E>, B>
            extends PageServiceIteratorBuilder<E, I, B> {

        protected SingleCallServiceIteratorBuilder(SpewApplication<?> application, Class<E> elementType,
                I iteratorInstance) {
            super(application, elementType, iteratorInstance);
        }

    }

}
