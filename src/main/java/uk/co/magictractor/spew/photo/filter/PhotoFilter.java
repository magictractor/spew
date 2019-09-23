package uk.co.magictractor.spew.photo.filter;

import java.util.function.Predicate;

import uk.co.magictractor.spew.photo.Image;

/**
 * PhotoFilter implementations can be used by some {@link PagedServiceInterator}
 * implementations to modify service request parameters, potentially reducing
 * the amount of data returned. When the iterator implementation does not
 * support the PhotoFilter, the PhotoFilter can still be used to filter the
 * results returned from the service. TODO! should be used to build filtered
 * page service calls.
 */
public interface PhotoFilter extends Predicate<Image> {

}
