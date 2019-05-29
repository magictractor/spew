package uk.co.magictractor.oauth.processor;

import java.time.LocalDate;

// TODO! bin this??
public interface DateAwareProcessorContext<I, E> extends ProcessorContext<I, E> {

    LocalDate getDate(E element);

    default void beforeDate(LocalDate date) {
        System.err.println("beforeDate " + date);
    }

    default void afterDate(LocalDate date) {
        System.err.println("afterDate " + date);
    }

}
