package uk.co.magictractor.spew.core.typeadapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class LocalDateTimeTypeAdapter implements SpewTypeAdapter<LocalDateTime> {

    private final DateTimeFormatter formatter;

    public LocalDateTimeTypeAdapter(String format) {
        // Google has time zone "2018-11-20T15:09:42Z"
        // Flickr: "yyyy-MM-dd HH:mm:ss"
        formatter = new DateTimeFormatterBuilder().appendPattern(format).toFormatter();
    }

    @Override
    public Class<LocalDateTime> getType() {
        return LocalDateTime.class;
    }

    @Override
    public LocalDateTime fromString(String valueAsString) {
        return formatter.parse(valueAsString, LocalDateTime::from);
    }

    @Override
    public String toString(LocalDateTime value) {
        return formatter.format(value);
    }

}
