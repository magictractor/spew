package uk.co.magictractor.spew.photo.local;

import java.util.function.Supplier;

// Supplier with a description for logging
public interface PhotoPropertiesSupplier<T> extends Supplier<T> {
    String getDescription();

    public static <T> PhotoPropertiesSupplier<T> of(Supplier<T> getter, String description) {
        return new PhotoPropertiesSupplier<T>() {

            @Override
            public T get() {
                return getter.get();
            }

            @Override
            public String getDescription() {
                return description;
            }
        };
    }
}
