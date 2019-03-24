package uk.co.magictractor.oauth.api;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.AbstractIterator;

import uk.co.magictractor.oauth.common.filter.DateTakenPhotoFilter;
import uk.co.magictractor.oauth.common.filter.DateUploadedPhotoFilter;
import uk.co.magictractor.oauth.common.filter.PhotoFilter;

public abstract class PageServiceIterator<E> extends AbstractIterator<E> {

	private List<? extends E> currentPage = Collections.emptyList();
	private int nextPageItemIndex;

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

	public void addFilter(PhotoFilter filter) {
		if (!supportedPhotoFilters().contains(filter.getClass())) {
			throw new UnsupportedOperationException("Filter type " + filter.getClass().getSimpleName()
					+ " is not supported. Use a TODO! to apply the filter instead.");
		}

		if (filter instanceof DateTakenPhotoFilter) {
			setDateTakenPhotoFilter((DateTakenPhotoFilter) filter);
		} else if (filter instanceof DateUploadedPhotoFilter) {
			setDateUploadedPhotoFilter((DateUploadedPhotoFilter) filter);
		} 
		throw new UnsupportedOperationException(
				"Code must be modified to handle filter type " + filter.getClass().getSimpleName());
	}

	protected void setDateTakenPhotoFilter(DateTakenPhotoFilter filter) {
		throw new UnsupportedOperationException(
				"setDateTakenPhotoFilter() must be overridden when DateTakenPhotoFilter is supported");
	}
	
	protected void setDateUploadedPhotoFilter(DateUploadedPhotoFilter filter) {
		throw new UnsupportedOperationException(
				"setDateUploadedPhotoFilter() must be overridden when DateUploadedPhotoFilter is supported");
	}

	public abstract Collection<Class<? extends PhotoFilter>> supportedPhotoFilters();
}
