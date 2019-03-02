package uk.co.magictractor.oauth.processor;

// <I> element from an iterator
// <E> processed element
public interface ProcessorContext<I, E> {

	// Called before the first element.
	default void beforeProcessing() {
		System.err.println("beforeProcessing");
	}

	// Called before all of the processors in the processor chain are run against an element.
	//
	// Implementations will often return the parameter, but some implementations
	// will return another object, such as a mutable copy of the parameter.
	E beforeElement(I base);

	// Called after all of the processors in the processor chain are run against an element.
	void afterElement(E element);

	// Called after the last element.
	default void afterProcessing() {
		System.err.println("afterProcessing");
	}
}
