package uk.co.magictractor.oauth.api;

import java.util.Iterator;
import java.util.function.Predicate;

import com.google.common.collect.Iterators;

import uk.co.magictractor.oauth.common.Photo;
import uk.co.magictractor.oauth.common.filter.PhotoFilter;

/**
 * Builder which applies iterator filters in the service request when possible,
 * and otherwise filters the results from the service.
 */
public class PhotoIteratorBuilder<P extends Photo> {

    private final PhotoIterator<P> wrapped;
    private Iterator<P> filteredIterator;

    public PhotoIteratorBuilder(PhotoIterator<P> wrapped) {
        this.wrapped = wrapped;
        this.filteredIterator = wrapped;
    }

    public PhotoIteratorBuilder<P> withFilter(Predicate<? super P> filter) {
        if (filter instanceof PhotoFilter && wrapped.supportsFilter((PhotoFilter) filter)) {
            // The filter is applied to the request parameters, reducing the number of
            // photos fetched by the service provider.
            wrapped.addFilter((PhotoFilter) filter);
        }
        else {
            // The service will fetch photos we don't care about photos which don't match
            // the predicate will be discarded.
            // Note that this method's param is a Java Predicate, Iterators.filter() is
            // based a Google Predicate.
            filteredIterator = Iterators.filter(filteredIterator, (p) -> filter.test(p));
        }
        return this;
    }

    public Iterator<P> build() {
        return filteredIterator;
    }
}
