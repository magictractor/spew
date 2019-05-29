package uk.co.magictractor.oauth.processor;

public class SimpleProcessorContext<E> implements ProcessorContext<E, E> {

    public E beforeElement(E base) {
        return base;
    }

    @Override
    public void afterElement(E element) {
    }

}
