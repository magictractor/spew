package uk.co.magictractor.oauth.api;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.AbstractIterator;

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

}
