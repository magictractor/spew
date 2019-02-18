package uk.co.magictractor.oauth.processor;

public interface ProcessorContext<E, D extends Changes<E>> {

	default void beforeProcessing() {
		System.err.println("beforeProcessing");
	}

	D beforeElement(E element);

	default void afterElement(Changes<E> changes) {
		changes.persist();
	}

	default void afterProcessing() {
		System.err.println("afterProcessing");
	}
}
