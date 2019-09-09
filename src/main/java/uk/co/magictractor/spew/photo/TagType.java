package uk.co.magictractor.spew.photo;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.MoreObjects;

public class TagType implements Comparable<TagType> {

    private static final Map<String, TagType> INSTANCES = new LinkedHashMap<>();

    private static AtomicInteger nextOrder = new AtomicInteger();

    static {
        // TagTypes are initialised from resource files along with Tags
        Tag.fetchTagIfPresent("something");
    }

    private final String name;
    private final int order;
    private final boolean optional;

    private TagType(String name, Map<String, String> properties) {
        this.name = name;
        this.order = nextOrder.getAndIncrement();
        this.optional = "true".equals(properties.get("optional"));
    }

    public String getName() {
        return name;
    }

    public boolean isOptional() {
        return optional;
    }

    @Override
    public int compareTo(TagType other) {
        return order - other.order;
    }

    public static TagType fetch(String name) {
        TagType tagType = INSTANCES.get(name);
        if (tagType == null) {
            throw new IllegalArgumentException(TagType.class.getSimpleName() + " with name '" + name + "' not found");
        }

        return tagType;
    }

    public static TagType fetchOrCreate(String name, Map<String, String> newInstanceProperties) {
        TagType tagType = INSTANCES.get(name);
        if (tagType == null) {
            tagType = new TagType(name, newInstanceProperties);
            INSTANCES.put(name, tagType);
        }

        return tagType;
    }

    public static Collection<TagType> values() {
        return INSTANCES.values();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("name", name).toString();
    }

}
