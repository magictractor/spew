package uk.co.magictractor.spew.processor;

public interface Processor<I, E, C extends ProcessorContext<I, E>> {

    void process(E element, C context);

}
