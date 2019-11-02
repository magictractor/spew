package uk.co.magictractor.spew.core.typeadapter;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class InstantTypeAdapter implements SpewTypeAdapter<Instant> {

    private final DateTimeFormatter formatter;

    public InstantTypeAdapter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public Class<Instant> getType() {
        return Instant.class;
    }

    @Override
    public Instant fromString(String valueAsString) {
        return formatter.parse(valueAsString, Instant::from);
    }

    @Override
    public String toString(Instant value) {
        return formatter.format(value);
    }

}
