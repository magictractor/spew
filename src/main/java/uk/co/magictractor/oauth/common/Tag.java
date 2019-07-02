package uk.co.magictractor.oauth.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import com.google.common.base.MoreObjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tag {

    private static Logger LOGGER = LoggerFactory.getLogger(Tag.class);

    public static Comparator<Tag> TAG_NAME_COMPARATOR = Comparator.comparing(Tag::getTagName);

    private static final Map<String, Tag> TAG_MAP = new HashMap<>();

    static {
        initTags();
    }

    private final TagType tagType;
    private final Tag parent;
    private final String tagName;
    // Lowercase, spaces and punctation stripped (Flickr specific)
    private final String canonicalTagName;
    private final int depth;
    private List<String> aliases = Collections.emptyList();
    private final List<Tag> children = new ArrayList<>();

    public static Tag createRoot(TagType tagType, String tagName) {
        System.err.println("create root: " + tagName);
        return new Tag(tagType, null, tagName, 0);
    }

    public static Tag createChild(Tag parent, String tagName) {
        System.err.println("create child: " + tagName + " with parent " + parent.tagName);
        return new Tag(parent.tagType, parent, tagName, parent.depth + 1);
    }

    private Tag(TagType tagType, Tag parent, String tagName, int depth) {
        this.tagType = tagType;
        this.parent = parent;
        this.tagName = tagName;
        this.canonicalTagName = canonicalName(tagName);
        this.depth = depth;

        if (parent != null) {
            parent.children.add(this);
        }

        addTagToMap(this.canonicalTagName, this);
    }

    public void addAlias(String alias) {
        addTagToMap(canonicalName(alias), this);

        if (aliases.isEmpty()) {
            // Replace with mutable list.
            aliases = new ArrayList<>();
        }
        aliases.add(alias);
    }

    public TagType getTagType() {
        return tagType;
    }

    public Tag getParent() {
        return parent;
    }

    public String getTagName() {
        return tagName;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public int getDepth() {
        return depth;
    }

    public boolean isUnknown() {
        return tagType == null;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    private static String canonicalName(String name) {
        StringBuilder canonicalNameBuilder = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isLetter(c)) {
                canonicalNameBuilder.append(Character.toLowerCase(c));
            }
        }

        return canonicalNameBuilder.toString();
    }

    private static void initTags() {
        ServiceLoader.load(TagLoader.class).forEach((loader) -> {
            LOGGER.debug("loading tags with {}", loader);
            loader.loadTags();
        });
    }

    private static void addTagToMap(String key, Tag tag) {
        if (TAG_MAP.containsKey(key)) {
            throw new IllegalStateException("Tag already exists with canonical name: " + key);
        }

        TAG_MAP.put(key, tag);
    }

    public static Tag fetchOrCreateTag(String tagName) {
        return fetchOrCreateTagCanonical(canonicalName(tagName));
    }

    public static Tag fetchOrCreateTagCanonical(String canonicalTagName) {
        if (!TAG_MAP.containsKey(canonicalTagName)) {
            Tag tag = new Tag(null, null, canonicalTagName, 0);
            return tag;
        }
        return TAG_MAP.get(canonicalTagName);
    }

    public static Tag fetchTag(String tagName) {
        return fetchTagCanonical(canonicalName(tagName));
    }

    // For use with Flickr or other service providers which use the same
    // TODO! perhaps have SPI for tag canonical form?
    public static Tag fetchTagCanonical(String canonicalTagName) {
        if (!TAG_MAP.containsKey(canonicalTagName)) {
            throw new IllegalArgumentException("No tag has compact name '" + canonicalTagName + "'");
        }
        return TAG_MAP.get(canonicalTagName);
    }

    public static Tag fetchTagIfPresent(String tagName) {
        return fetchTagIfPresentCanonical(canonicalName(tagName));
    }

    public static Tag fetchTagIfPresentCanonical(String canonicalTagName) {
        return TAG_MAP.get(canonicalTagName);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("tagName", tagName).toString();
    }

}
