package uk.co.magictractor.oauth.common;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.MoreObjects;

public class TagType implements Comparable<TagType> {

    private static final Map<String, TagType> INSTANCES = new LinkedHashMap<>();

    private static AtomicInteger nextOrder = new AtomicInteger();

    private final String name;
    private final int order;

    private TagType(String name) {
        this.name = name;
        this.order = nextOrder.getAndIncrement();
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(TagType other) {
        return order - other.order;
    }

    public static TagType valueOf(String name) {
        TagType tagType = INSTANCES.get(name);
        if (tagType == null) {
            tagType = new TagType(name);
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
