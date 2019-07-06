package uk.co.magictractor.oauth.local;

import uk.co.magictractor.spew.local.PhotoPropertiesSupplier;

/**
 * Trivial PhotoPropertiesSupplier implementation for use by unit tests.
 */
public class SimplePhotoPropertiesSupplier<T> implements PhotoPropertiesSupplier<T> {

    private final T value;

    public static final SimplePhotoPropertiesSupplier<?> NULL = new SimplePhotoPropertiesSupplier<>(null);

    @SuppressWarnings("unchecked")
    public static <T> SimplePhotoPropertiesSupplier<T> of(T value) {
        if (value == null) {
            return (SimplePhotoPropertiesSupplier<T>) NULL;
        }
        else {
            return new SimplePhotoPropertiesSupplier<>(value);
        }
    }

    private SimplePhotoPropertiesSupplier(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public String getDescription() {
        return "SimplePhotoPropertiesSupplier[value=" + value + "]";
    }

}
