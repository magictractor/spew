package uk.co.magictractor.oauth.processor;

public interface Processor<E, C extends ProcessorContext<E, D>, D extends Changes<E>> {

	void process(D changes, C context);

}
