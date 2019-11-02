package uk.co.magictractor.spew.core.typeadapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

// TODO! do not use LocalDateTime (unless timezone is unknown)
// LocalDateTime does NOT have a timezone
// https://stackoverflow.com/questions/32437550/whats-the-difference-between-instant-and-localdatetime/32443004
// Use InstantTypeAdapter instead
@Deprecated(forRemoval = true)
public class LocalDateTimeTypeAdapter implements SpewTypeAdapter<LocalDateTime> {

    private final DateTimeFormatter formatter;

    public LocalDateTimeTypeAdapter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    // Perhaps bin this in favour of DateTimeFormatter constructor.
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
