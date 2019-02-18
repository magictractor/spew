package uk.co.magictractor.oauth.flickr.pojo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tag {

	public static Comparator<Tag> TAG_NAME_COMPARATOR = Comparator.comparing(Tag::getTagName);
	// private static Comparator<Tag> TAG_TYPE_COMPARATOR =
	// Comparator.comparing(Tag::getTagType);

	private static final Map<String, Tag> TAG_MAP = new HashMap<>();

	static {
		initTags();
	}

	private final TagType tagType;
	private final Tag parent;
	private final String tagName;
	// Lowercase, spaces and punctation stripped
	private final String compactTagName;
	private final int depth;
	private final List<Tag> children = new ArrayList<>();

	private Tag(TagType tagType, String tagName) {
		this(tagType, null, tagName, 0);
	}

	private Tag(Tag parent, String tagName) {
		this(parent.tagType, parent, tagName, parent.depth + 1);
	}

	private Tag(TagType tagType, Tag parent, String tagName, int depth) {
		this.tagType = tagType;
		this.parent = parent;
		this.tagName = tagName;
		this.compactTagName = compactName(tagName);
		this.depth = depth;

		if (parent != null) {
			parent.children.add(this);
		}
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

	public String getCompactTagName() {
		return compactTagName;
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

	private String compactName(String name) {
		StringBuilder compactNameBuilder = new StringBuilder();
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (Character.isLetter(c)) {
				compactNameBuilder.append(Character.toLowerCase(c));
			}
		}

		return compactNameBuilder.toString();
	}

	private static void initTags() {
		initLocations();
		initSubjects();
	}

	private static void initLocations() {
		Tag edinburgh = init(TagType.LOCATION, "Edinburgh");
		Tag fife = init(TagType.LOCATION, "Fife");
		init(TagType.LOCATION, "Esk", "Musselburgh lagoons", "Roslin glen");

		init(edinburgh, "RBGE", "Almond", "Balgreen", "Bawsinch", "Calton Hill", "Cammo", "Corstorphine Hill",
				"Fountainbridge", "Figgate Park", "Holyrood Park", "Inverleith Park", "Lochend Park",
				"Montgomery Street Park", "Pilrig Park", "Portobello", "Princes Street Gardens", "Straighton",
				"Water of Leith");
	}

	private static void initSubjects() {
		initBirds();
		initRodents();
		initInsects();

		// TODO! fungus?
		init(TagType.SUBJECT, "Fungi");
	}

	private static void initBirds() {
		Tag bird = init(TagType.SUBJECT, "Bird");
		init(bird, "Blackbird", "Blackcap", "Blue tit", "Bullfinch", "Buzzard", "Chaffinch", "Coal Tit", "Coot",
				"Dipper", "Dunnock", "Fieldfare", "Goldcrest", "Goldeneye", "Goldfinch", "Goosander", "Great tit",
				"Kestrel", "Kingfisher", "Linnet", "Long-tailed tit", "Mallard", "Moorhen", "Nuthatch", "Oystercatcher",
				"Pochard", "Redshank", "Redwing", "Robin", "Shag", "Siskin", "Sparrowhawk", "Starling", "Swallow",
				"Teal", "Treecreeper", "Tufted duck", "Water rail", "Waxwing", "Wood pigeon", "Wren");

		Tag crow = init(bird, "Crow");
		init(crow, "Carrion crow");

		Tag grebe = init(bird, "Grebe");
		init(grebe, "Little grebe", "Great crested grebe");

		Tag heron = init(bird, "Heron");
		init(heron, "Grey heron");

		Tag owl = init(bird, "Owl");
		init(owl, "Tawny owl");

		Tag pipit = init(bird, "Pipit");
		init(pipit, "Meadow Pipit");

		Tag sparrow = init(bird, "Sparrow");
		init(sparrow, "House sparrow");

		Tag swan = init(bird, "Swan");
		init(swan, "Mute swan");

		Tag wagtail = init(bird, "Wagtail");
		init(wagtail, "Grey wagtail", "Pied wagtail");

		Tag whiteThroat = init(bird, "White throat");
		init(whiteThroat, "Common white throat");

		Tag woodpecker = init(bird, "Woodpecker");
		init(woodpecker, "Green woodpecker", "Great spotted woodpecker");
		// gulls and woodpeckers and swans and warblers
		// "Black-headed gull",
	}

	private static void initRodents() {
		// Not a rodent!
		init(TagType.SUBJECT, "Otter");

		Tag rodent = init(TagType.SUBJECT, "Rodent");

		Tag squirrel = init(rodent, "Squirrel");
		init(squirrel, "Grey squirrel", "Red squirrel");

		Tag rat = init(rodent, "Rat");
		init(rat, "Brown rat");
	}

	private static void initInsects() {
		Tag insect = init(TagType.SUBJECT, "Insect");
		init(insect, "Caddisfly");

		Tag bee = init(insect, "Bee");
		init(bee, "Honey bee");
		Tag bumblebee = init(bee, "Bumblebee");
		init(bumblebee, "Common carder bee", "Tree bumblebee");

		Tag lepidoptera = init(insect, "Lepidoptera");

		Tag butterfly = init(lepidoptera, "Butterfly");
		init(butterfly, "Peacock", "Green veined white", "Speckled wood", "Small copper", "Red admiral", "Comma",
				"Painted lady", "Ringlet", "Small white");

		Tag moth = init(lepidoptera, "Moth");
		init(moth, "Silver Y");

		Tag odonata = init(insect, "Odonata");
		Tag damselfly = init(odonata, "Damselfly");
		Tag dragonfly = init(odonata, "Dragonfly");
		init(damselfly, "Blue-tailed damselfly", "Common blue damselfly", "Azure damselfly", "Emerald damselfly",
				"Large red damselfly");
		init(dragonfly, "Common darter", "Black darter", "Common hawker", "Four-spotted chaser");
	}

	private static void init(TagType tagType, String... tagNames) {
		for (String tagName : tagNames) {
			init(tagType, tagName);
		}
	}

	private static Tag init(TagType tagType, String tagName) {
		Tag tag = new Tag(tagType, tagName);
		addTag(tag);
		return tag;
	}

	private static void init(Tag parentTag, String... tagNames) {
		for (String tagName : tagNames) {
			init(parentTag, tagName);
		}
	}

	private static Tag init(Tag parentTag, String tagName) {
		Tag tag = new Tag(parentTag, tagName);
		addTag(tag);
		return tag;
	}

	private static void addTag(Tag tag) {
		if (TAG_MAP.containsKey(tag.compactTagName)) {
			throw new IllegalStateException("Tag already exists with compact name: " + tag.compactTagName);
		}

		TAG_MAP.put(tag.compactTagName, tag);
	}

	public static Tag fetchOrCreateTag(String compactTagName) {
		if (!TAG_MAP.containsKey(compactTagName)) {
			Tag tag = new Tag(null, null, compactTagName, 0);
			addTag(tag);
			return tag;
		}
		return TAG_MAP.get(compactTagName);
	}

	public static Tag fetchTag(String compactTagName) {
		if (!TAG_MAP.containsKey(compactTagName)) {
			throw new IllegalArgumentException("No tag has compact name '" + compactTagName + "'");
		}
		return TAG_MAP.get(compactTagName);
	}

	public static Tag fetchTagIfPresent(String compactTagName) {
		return TAG_MAP.get(compactTagName);
	}

	@Override
	public String toString() {
		return "Tag [compactTagName=" + compactTagName + ", depth=" + depth + "]";
	}

}
