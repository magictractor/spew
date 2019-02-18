package uk.co.magictractor.oauth.processor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProcessorChain<E, C extends ProcessorContext<E, D>, D extends Changes<E>> {

	private final List<Processor<E, C, D>> processors = new ArrayList<>();

	protected void addProcessor(Processor<E, C, D> processor) {
		processors.add(processor);
	}

	protected final void execute(Iterator<E> iterator, C context) {
		boolean isDateAware = context instanceof DateAwareProcessorContext;
		LocalDate date = null;
		LocalDate previousDate = null;
		DateAwareProcessorContext<E, D> dateAwareContext = isDateAware ? ((DateAwareProcessorContext<E, D>) context)
				: null;

		context.beforeProcessing();

		while (iterator.hasNext()) {
			E element = iterator.next();
			D changes = context.beforeElement(element);

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

			process(context, changes);

			context.afterElement(changes);

			if (isDateAware) {
				previousDate = date;
			}
		}

		if (isDateAware) {
			dateAwareContext.afterDate(previousDate);
		}

		context.afterProcessing();
	}

	private void process(C context, D changes) {

		for (Processor<E, C, D> processor : processors) {
			processor.process(changes, context);
		}

	}

}
