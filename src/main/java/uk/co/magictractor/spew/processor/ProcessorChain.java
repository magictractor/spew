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
        LocalDate date = null;
        LocalDate previousDate = null;

        context.beforeProcessing();

        while (iterator.hasNext()) {
            E element = context.beforeElement(iterator.next());

            for (Processor<? super I, E, C> processor : processors) {
                processor.process(element, context);
            }

            context.afterElement(element);
        }

        for (Processor<? super I, E, C> processor : processors) {
            processor.afterProcessing(context);
        }

        // TODO! remove this from context??
        context.afterProcessing();
    }

}
