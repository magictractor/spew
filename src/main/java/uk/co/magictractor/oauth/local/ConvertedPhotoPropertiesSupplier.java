package uk.co.magictractor.oauth.local;

import java.util.function.Function;

import uk.co.magictractor.oauth.local.PropertySuppliedPhoto.PhotoPropertiesSupplier;

public class ConvertedPhotoPropertiesSupplier<T> implements PhotoPropertiesSupplier<T> {

	private PhotoPropertiesSupplier<String> wrapped;
	private Function<String, T> converter;

	public ConvertedPhotoPropertiesSupplier(PhotoPropertiesSupplier<String> wrapped, Function<String, T> converter) {
		this.wrapped = wrapped;
		this.converter = converter;
	}

	@Override
	public T get() {
		String string = wrapped.get();
		return string == null ? null : converter.apply(string);
	}

	@Override
	public String getDescription() {
		return wrapped.getDescription();
	}

	public static PhotoPropertiesSupplier<Integer> asInteger(PhotoPropertiesSupplier<String> stringSupplier) {
		return new ConvertedPhotoPropertiesSupplier<Integer>(stringSupplier, (s) -> Integer.valueOf(s));
	}
}
