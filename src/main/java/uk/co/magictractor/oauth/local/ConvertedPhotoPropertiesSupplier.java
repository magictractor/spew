package uk.co.magictractor.oauth.local;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import com.google.common.collect.Iterables;

public class ConvertedPhotoPropertiesSupplier<FROM, TO> implements PhotoPropertiesSupplier<TO> {

	private PhotoPropertiesSupplier<FROM> wrapped;
	private Function<FROM, TO> converter;

	public ConvertedPhotoPropertiesSupplier(PhotoPropertiesSupplier<FROM> wrapped, Function<FROM, TO> converter) {
		this.wrapped = wrapped;
		this.converter = converter;
	}

	@Override
	public TO get() {
		FROM from = wrapped.get();
		return from == null ? null : converter.apply(from);
	}

	@Override
	public String getDescription() {
		return wrapped.getDescription();
	}

	public static PhotoPropertiesSupplier<Integer> asInteger(PhotoPropertiesSupplier<String> stringSupplier) {
		return new ConvertedPhotoPropertiesSupplier<String, Integer>(stringSupplier,
				(string) -> Integer.valueOf(string));
	}

	public static PhotoPropertiesSupplier<Instant> asInstant(PhotoPropertiesSupplier<Date> dateSupplier) {
		return new ConvertedPhotoPropertiesSupplier<Date, Instant>(dateSupplier, (date) -> date.toInstant());
	}

	public static <T> PhotoPropertiesSupplier<T> onlyElement(PhotoPropertiesSupplier<List<T>> listSupplier) {
		return new ConvertedPhotoPropertiesSupplier<List<T>, T>(listSupplier,
				(list) -> Iterables.getOnlyElement(list, null));
	}

//	public static <T> PhotoPropertiesSupplier<T> onlyElement(PhotoPropertiesSupplier<Stream<T>> streamSupplier) {
//		return new ConvertedPhotoPropertiesSupplier<Stream<T>, T>(streamSupplier,
//				(stream) -> Iterators.getOnlyElement(stream.iterator(), null));
//	}
}
