package uk.co.magictractor.spew.processor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProcessorChain<I, E, C extends ProcessorContext<? super I, E>> {

    private final List<Processor<? super I, E, C>> processors = new ArrayList<>();

    protected void addProcessor(Processor<? super I, E, C> processor) {
        processors.add(processor);
    }

    public final void execute(Iterator<? extends I> iterator, C context) {
        boolean isDateAware = context instanceof DateAwareProcessorContext;
        LocalDate date = null;
        LocalDate previousDate = null;
        // TODO! don't like DateAwareProcessorContext
        DateAwareProcessorContext<I, E> dateAwareContext = isDateAware ? ((DateAwareProcessorContext<I, E>) context)
                : null;

        context.beforeProcessing();

        while (iterator.hasNext()) {
            E element = context.beforeElement(iterator.next());

            // TODO! doc what this is used for
            // TODO! should be able to handle this in beforeElement impl?
            if (isDateAware) {
                date = dateAwareContext.getDate(element);
                if (date == null) {
                    throw new IllegalStateException();
                }
                if (!date.equals(previousDate)) {
                    if (previousDate != null) {
                        dateAwareContext.afterDate(previousDate);
                    }
                    dateAwareContext.beforeDate(date);
                }
            }

            for (Processor<? super I, E, C> processor : processors) {
                processor.process(element, context);
            }

            context.afterElement(element);

            if (isDateAware) {
                previousDate = date;
            }
        }

        if (isDateAware) {
            dateAwareContext.afterDate(previousDate);
        }

        for (Processor<? super I, E, C> processor : processors) {
            processor.afterProcessing(context);
        }

        // TODO! remove this from context??
        context.afterProcessing();
    }

}
