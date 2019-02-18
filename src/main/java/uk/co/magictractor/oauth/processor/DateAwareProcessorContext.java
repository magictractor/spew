package uk.co.magictractor.oauth.processor;

import java.time.LocalDate;

public interface DateAwareProcessorContext<E, D extends Changes<E>> extends ProcessorContext<E, D> {

	LocalDate getDate(E element);
	
	default void beforeDate(LocalDate date) {
		System.err.println("beforeDate " + date);
	}

	default void afterDate(LocalDate date) {
		System.err.println("afterDate " + date);
	}
	
}
