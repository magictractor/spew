package uk.co.magictractor.oauth.api;

import java.util.Collections;
import java.util.List;

/**
 * Base class for iterators which fetch items using third party service methods
 * which return a page of items, and service requests are passed a page number.
 * 
 * The first page is fetched on the first use of {@link #hasNext()} or
 * {@link #next()}, and the next page is fetched only after iterating over all
 * items in the first page.
 */
public abstract class PageCountServiceIterator<E> extends PageServiceIterator<E> {

	private static final int UNKNOWN = -1;

	private final OAuthApplication application;

	private int nextPageNumber = 1;

	private int totalPageCount = UNKNOWN;
	private int totalItemCount = UNKNOWN;

	// TODO! also pass in expected service provider and add assertion??
	public PageCountServiceIterator(OAuthApplication application) {
		this.application = application;
	}

	protected OAuthConnection getConnection() {
		return application.getConnection();
	}

	@Override
	protected List<E> nextPage() {
		/*
		 * If known, check the expected number of pages. Some services (such as Flickr
		 * getPhotos()) will repeat the last page of items if the page number is too
		 * big.
		 */
		// TODO! could check total item count too
		if (totalPageCount != UNKNOWN && nextPageNumber > totalPageCount) {
			// TODO! logging
			return Collections.emptyList();
		} else {
			return fetchPage(nextPageNumber++);
		}
	}

	// pageNumber is one based in this iterator. If the service uses zero based page
	// numbering, just subtract one from the parameter value.
	protected abstract List<E> fetchPage(int pageNumber);

	public int getTotalPageCount() {
		return totalPageCount;
	}

	protected void setTotalPageCount(int totalPageCount) {
		if (this.totalPageCount == UNKNOWN) {
			this.totalPageCount = totalPageCount;
		} else if (this.totalPageCount != totalPageCount) {
			throw new IllegalStateException("totalPageCount should not be changed after it has been set");
		}
	}

	public int getTotalItemCount() {
		return totalItemCount;
	}

	protected void setTotalItemCount(int totalItemCount) {
		if (this.totalItemCount == UNKNOWN) {
			this.totalItemCount = totalItemCount;
		} else if (this.totalItemCount != totalItemCount) {
			throw new IllegalStateException("totalItemCount should not be changed after it has been set");
		}
	}
}
