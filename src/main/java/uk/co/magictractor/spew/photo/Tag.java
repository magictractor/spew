package uk.co.magictractor.spew.photo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.MoreObjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.util.spi.SPIUtil;

public class Tag {

    private static Logger LOGGER = LoggerFactory.getLogger(Tag.class);

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
        // System.err.println("create root: " + tagName);
        return new Tag(tagType, null, tagName, 0);
    }

    public static Tag createChild(Tag parent, String tagName) {
        // System.err.println("create child: " + tagName + " with parent " + parent.tagName);
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

    public List<String> getTagNameAndAliases() {
        if (aliases.isEmpty()) {
            return Collections.singletonList(tagName);
        }
        else {
            int aliasCount = aliases.size();
            List<String> tagNameAndAliases = new ArrayList<>(aliasCount + 1);
            tagNameAndAliases.add(tagName);
            tagNameAndAliases.addAll(aliases);
            return tagNameAndAliases;
        }
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
            // Strip whitespace and hyphens
            // Punctuation permitted for machine tag style like "irecord:id=1234"
            if (!Character.isWhitespace(c) && c != '-') {
                canonicalNameBuilder.append(Character.toLowerCase(c));
            }
        }

        return canonicalNameBuilder.toString();
    }

    private static void initTags() {
        SPIUtil.available(TagLoader.class).forEach(TagLoader::loadTags);
    }

    private static void addTagToMap(String key, Tag tag) {
        if (TAG_MAP.containsKey(key)) {
            throw new IllegalStateException("Tag already exists with canonical name: " + key);
        }

        TAG_MAP.put(key, tag);
    }

    public static Tag fetchOrCreateTag(String tagName) {
        String canonicalTagName = canonicalName(tagName);
        Tag tag = TAG_MAP.get(canonicalTagName);
        if (tag == null) {
            tag = new Tag(null, null, tagName, 0);
            // Do not add to the map, that was done in the constructor.
        }
        return tag;
    }

    public static Tag fetchTag(String tagName) {
        String canonicalTagName = canonicalName(tagName);
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

    public static Collection<Tag> fetchTags(TagType tagType) {
        return tagStream(tagType).collect(Collectors.toList());
    }

    public static Collection<Tag> fetchTerminalTags(TagType tagType) {
        return tagStream(tagType)
                .filter(tag -> !tag.hasChildren())
                .collect(Collectors.toList());
    }

    private static Stream<Tag> tagStream(TagType tagType) {
        return TAG_MAP.values().stream().filter(tag -> tagType.equals(tag.getTagType()));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("tagName", tagName).toString();
    }

}
