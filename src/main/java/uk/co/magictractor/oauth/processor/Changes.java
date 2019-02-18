package uk.co.magictractor.oauth.processor;

public interface Changes<E> {

	/**
	 * Call API methods to persist changes made by processors.
	 * 
	 * implementations should ensure that methods are called only for values which
	 * have changed. For example, if a title has not changed, the method for setting
	 * the title should not be called.
	 */
	void persist();

}
