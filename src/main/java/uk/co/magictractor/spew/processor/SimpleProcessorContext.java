package uk.co.magictractor.spew.processor;

public class SimpleProcessorContext<E> implements ProcessorContext<E, E> {

    public E beforeElement(E base) {
        return base;
    }

    @Override
    public void afterElement(E element) {
    }

}
