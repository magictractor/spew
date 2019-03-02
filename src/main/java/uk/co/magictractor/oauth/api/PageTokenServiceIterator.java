package uk.co.magictractor.oauth.api;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Base class for iterators which fetch items using third party service methods
 * which return a page of items, and service requests are passed a page token
 * for pages after the first page.
 * 
 * The first page is fetched on the first use of {@link #hasNext()} or
 * {@link #next()}, and the next page is fetched only after iterating over all
 * items in the first page.
 */
public abstract class PageTokenServiceIterator<E> implements Iterator<E> {

	private static final int UNKNOWN = -1;

	private List<? extends E> currentPage = null;
	private String nextPageToken = null;
	private int nextPageItemIndex;
	private boolean hasNext;

	// TODO! some of this code is common with the page count iterator
	@Override
	public boolean hasNext() {
		ensurePage();
		return hasNext;
	}

	@Override
	public E next() {
		ensurePage();
		if (!hasNext) {
			throw new NoSuchElementException();
		}
		return currentPage.get(nextPageItemIndex++);
	}

	private void ensurePage() {
		if (currentPage == null || (nextPageItemIndex >= currentPage.size())) {
			if (currentPage != null && nextPageToken == null) {
				// At least one page has been fetched, and there is no token for another page.
				currentPage = Collections.emptyList();
			} else {
				currentPage = fetchPage(nextPageToken);
				if (currentPage == null) {
					throw new IllegalStateException("fetchPage() returned null");
				}
			}

			hasNext = !currentPage.isEmpty();
			nextPageItemIndex = 0;
		}
	}

	// BEWARE! this must (for now) set nextPageToken as a side effect - change
	// return type to include the nextPageToken
	protected abstract List<? extends E> fetchPage(String pageToken);

	protected void setNextPageToken(String nextPageToken) {
		this.nextPageToken = nextPageToken;
	}

}
