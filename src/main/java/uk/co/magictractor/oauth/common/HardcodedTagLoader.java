/**
 * Copyright 2015-2019 Ken Dobson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.magictractor.oauth.common;

/**
 * Initial TagLoader implementation for legacy code.
 */
// bin this once better approach has been coded
public class HardcodedTagLoader implements TagLoader {

    @Override
    public void loadTags() {
        initLocations();
        initSubjects();

        addAlias("wool carder bee", "Anthidium manicatum");
    }

    private void initLocations() {
        Tag edinburgh = init(TagType.LOCATION, "Edinburgh");
        Tag fife = init(TagType.LOCATION, "Fife");
        init(TagType.LOCATION, "Esk", "Musselburgh lagoons", "Roslin glen");

        init(edinburgh, "RBGE", "Almond", "Balgreen", "Bawsinch", "Calton Hill", "Cammo", "Corstorphine Hill",
            "Fountainbridge", "Figgate Park", "Holyrood Park", "Inverleith Park", "Lochend Park",
            "Montgomery Street Park", "Pilrig Park", "Portobello", "Princes Street Gardens", "Straighton",
            "Water of Leith");
    }

    private void initSubjects() {
        initBirds();
        initRodents();
        initInsects();

        Tag frog = init(TagType.SUBJECT, "Frog");
        init(frog, "Common frog");

        Tag newt = init(TagType.SUBJECT, "Newt");
        init(newt, "Smooth newt", "Palmate newt");

        // TODO! fungus?
        init(TagType.SUBJECT, "Fungi");
    }

    private void initBirds() {
        Tag bird = init(TagType.SUBJECT, "Bird");
        init(bird, "Blackbird", "Blackcap", "Blue tit", "Bullfinch", "Buzzard", "Chaffinch", "Coal tit",
            "Collared dove", "Coot", "Dipper", "Dunnock", "Fieldfare", "Goldcrest", "Goldeneye", "Goldfinch",
            "Goosander", "Great tit", "Kestrel", "Kingfisher", "Linnet", "Long-tailed tit", "Mallard", "Moorhen",
            "Nuthatch", "Oystercatcher", "Pochard", "Redshank", "Redwing", "Robin", "Shag", "Siskin", "Sparrowhawk",
            "Starling", "Swallow", "Teal", "Treecreeper", "Tufted duck", "Water rail", "Waxwing", "Wren");

        Tag crow = init(bird, "Crow");
        init(crow, "Carrion crow");

        Tag grebe = init(bird, "Grebe");
        init(grebe, "Little grebe", "Great crested grebe");

        Tag gull = init(bird, "Gull");
        init(gull, "Black-headed gull", "Lesser black-backed gull");

        Tag heron = init(bird, "Heron");
        init(heron, "Grey heron");

        Tag owl = init(bird, "Owl");
        init(owl, "Tawny owl");

        Tag pigeon = init(bird, "Pigeon");
        init(pigeon, "Feral pigeon", "Wood pigeon");

        Tag pipit = init(bird, "Pipit");
        init(pipit, "Meadow Pipit");

        Tag sparrow = init(bird, "Sparrow");
        init(sparrow, "House sparrow");

        Tag swan = init(bird, "Swan");
        init(swan, "Mute swan");

        Tag thrush = init(bird, "Thrush");
        init(thrush, "Mistle thrush", "Song thrush");

        Tag wagtail = init(bird, "Wagtail");
        init(wagtail, "Grey wagtail", "Pied wagtail");

        Tag warbler = init(bird, "Warbler");
        init(warbler, "Chiffchaff", "Willow warbler");

        Tag whiteThroat = init(bird, "White throat");
        init(whiteThroat, "Common white throat");

        Tag woodpecker = init(bird, "Woodpecker");
        init(woodpecker, "Green woodpecker", "Great spotted woodpecker");
        // gulls and woodpeckers and swans and warblers
        // "Black-headed gull",
    }

    private void initRodents() {
        // Not a rodent!
        init(TagType.SUBJECT, "Otter");

        Tag rodent = init(TagType.SUBJECT, "Rodent");

        Tag squirrel = init(rodent, "Squirrel");
        init(squirrel, "Grey squirrel", "Red squirrel");

        Tag rat = init(rodent, "Rat");
        init(rat, "Brown rat");
    }

    private void initInsects() {
        Tag insect = init(TagType.SUBJECT, "Insect");
        init(insect, "Caddisfly");

        Tag bee = init(insect, "Bee");
        init(bee, "Honey bee", "Early mining bee");
        Tag bumblebee = init(bee, "Bumblebee");
        init(bumblebee, "Common carder bee", "Tree bumblebee", "Red-tailed bumblebee", "Buff-tailed bumbleebee",
            "Wool carder bee");

        Tag wasp = init(insect, "Wasp");
        init(wasp, "Common wasp");

        Tag hoverfly = init(insect, "Hoverfly");
        init(hoverfly, "Footballer");
        Tag dronefly = init(hoverfly, "Drone fly");
        init(dronefly, "Common drone fly", "Tapered drone fly");

        Tag diptera = init(insect, "Diptera");
        init(diptera, "Tachina fera", "Tachina ursina");

        // TODO! alias "crane fly"
        Tag tipula = init(diptera, "Tipula");
        init(tipula, "Tipula vittata");
        // more

        Tag shieldbug = init(insect, "Shield bug");
        init(shieldbug, "Birch shield bug", "Gorse shield bug", "Hawthorn shield bug");

        Tag snail = init(insect, "Snail");
        init(snail, "Garden snail");

        Tag lepidoptera = init(insect, "Lepidoptera");

        Tag butterfly = init(lepidoptera, "Butterfly");
        init(butterfly, "Peacock", "Green veined white", "Large white", "Orange-tip", "Speckled wood", "Small copper",
            "Red admiral", "Comma", "Painted lady", "Ringlet", "Small white");

        //        Tag moth = init(lepidoptera, "Moth");
        //        init(moth, "Silver Y");
        //        init(moth, "Copper underwing");

        //        Tag odonata = init(insect, "Odonata");
        //        Tag damselfly = init(odonata, "Damselfly");
        //        Tag dragonfly = init(odonata, "Dragonfly");
        //        init(damselfly, "Blue-tailed damselfly", "Common blue damselfly", "Azure damselfly", "Emerald damselfly",
        //            "Large red damselfly");
        //        init(dragonfly, "Common darter", "Black darter", "Common hawker", "Four-spotted chaser");

        init(insect, "Mayfly");

        init(insect, "Slug");
    }

    private void init(TagType tagType, String... tagNames) {
        for (String tagName : tagNames) {
            init(tagType, tagName);
        }
    }

    private Tag init(TagType tagType, String tagName) {
        // Allow root to already have been defined in a resource file while
        // hardcoded values are migrated.
        Tag existing = Tag.fetchTagIfPresent(tagName);
        if (existing != null) {
            if (!tagType.equals(existing.getTagType())) {
                throw new IllegalStateException("Existing tag has different type");
            }
            if (existing.getParent() != null) {
                throw new IllegalStateException("Existing tag is not a root tag");
            }
            return existing;
        }

        return Tag.createRoot(tagType, tagName);
    }

    private void init(Tag parentTag, String... tagNames) {
        for (String tagName : tagNames) {
            init(parentTag, tagName);
        }
    }

    private Tag init(Tag parentTag, String tagName) {
        //        Tag tag = new Tag(parentTag, tagName);
        //        return tag;
        return Tag.createChild(parentTag, tagName);
    }

    private void addAlias(String tagName, String... aliases) {
        Tag tag = Tag.fetchOrCreateTag(tagName);
        for (String alias : aliases) {
            tag.addAlias(alias);
        }
    }

}
