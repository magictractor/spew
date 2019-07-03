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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import com.google.common.base.Splitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TagLoader which reads tag hierarchy and aliases from plain text config files
 * with hierarchy defined by indents.
 */
public class IndentedFileTagLoader implements TagLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndentedFileTagLoader.class);

    private final URL resource;

    private TagType tagType;
    private Deque<TagAndIndent> tagsAndIndents = new ArrayDeque<>();

    private boolean hasTabs;
    private boolean hasSpaces;

    public IndentedFileTagLoader() {
        // TODO! allow system property to override the default name and encoding (UTF-8 default)
        this(rootResource("tags/master.tags"));
    }

    private static URL rootResource(String resource) {
        // Use ClassLoader because the root resource should not be relative to this class.
        return IndentedFileTagLoader.class.getClassLoader().getResource(resource);
    }

    private IndentedFileTagLoader(URL resource) {
        this.resource = resource;
    }

    @Override
    public void loadTags() {
        try (InputStream fileStream = resource.openStream()) {
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8));
            fileReader.lines().forEach(this::handleLine);
        }
        catch (IOException e) {
            throw new UncheckedIOException("Cannot find tag resource: " + resource, e);
        }
    }

    private void handleLine(String line) {
        if (line.trim().isEmpty()) {
            return;
        }

        IndentAndRemainder indentAndRemainder = handleIndent(line);
        String remainder = indentAndRemainder.remainder;

        if (remainder.charAt(0) == '!') {
            handleNewTagType(remainder.substring(1).trim());
        }
        else if (remainder.charAt(0) == '+') {
            handleOtherFile(remainder.substring(1).trim());
        }
        else {
            handleChild(remainder, indentAndRemainder.indent);
        }
    }

    private void handleNewTagType(String tagTypeName) {
        tagType = TagType.valueOf(tagTypeName);
        tagsAndIndents.clear();
    }

    private void handleOtherFile(String otherFileName) {
        URL otherResource;
        try {
            otherResource = new URL(resource, otherFileName);
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }

        IndentedFileTagLoader other = new IndentedFileTagLoader(otherResource);
        other.tagType = tagType;
        if (!tagsAndIndents.isEmpty()) {
            /*
             * The current tags serves as a parent for the other file with
             * lowest possible indent so it cannot be popped while handling the
             * other file.
             */
            Tag otherRootTag = tagsAndIndents.peek().tag;
            other.tagsAndIndents.push(new TagAndIndent(otherRootTag, Integer.MIN_VALUE));
        }

        other.loadTags();
    }

    private void handleChild(String line, int indent) {
        if (tagType == null) {
            throw new IllegalStateException(
                "Missing tag type - tag types are on a line starting with an exclamation mark");
        }

        Iterator<String> tagNameAndAliases = Splitter.on("=")
                .trimResults()
                .splitToList(line)
                .iterator();

        String tagName = tagNameAndAliases.next();
        Tag tag;
        if (tagsAndIndents.isEmpty()) {
            tag = Tag.createRoot(tagType, tagName);
        }
        else {
            tag = Tag.createChild(tagsAndIndents.peek().tag, tagName);
        }

        while (tagNameAndAliases.hasNext()) {
            tag.addAlias(tagNameAndAliases.next());
        }

        tagsAndIndents.push(new TagAndIndent(tag, indent));
    }

    private IndentAndRemainder handleIndent(String line) {
        IndentAndRemainder indentAndRemainder = parseIndent(line);
        int indent = indentAndRemainder.indent;
        for (int i = tagsAndIndents.size(); i > 0; i--) {
            if (tagsAndIndents.peek().indent >= indent) {
                tagsAndIndents.pop();
            }
            else {
                break;
            }
        }

        return indentAndRemainder;
    }

    private IndentAndRemainder parseIndent(String line) {
        int i = 0;
        while (Character.isWhitespace(line.charAt(i))) {
            boolean isTab = (line.charAt(i) == '\t');
            if (isTab) {
                hasTabs = true;
            }
            else {
                hasSpaces = true;
            }

            if (hasTabs && hasSpaces) {
                throw new IllegalStateException("File mixes tabs and spaces - only one or the other should be used");
            }

            i++;
        }

        // Leading "<" reduces the indent, allowing negative indent
        // << bird
        // < sparrow
        // tree sparrow
        int indent = i;
        while (line.charAt(i) == '<') {
            i++;
            indent--;
        }

        return new IndentAndRemainder(indent, line.substring(i).trim());
    }

    private static final class IndentAndRemainder {
        private final int indent;
        private final String remainder;

        IndentAndRemainder(int indent, String remainder) {
            this.indent = indent;
            this.remainder = remainder;
        }
    }

    private static final class TagAndIndent {
        private final Tag tag;
        private final int indent;

        TagAndIndent(Tag tag, int indent) {
            this.tag = tag;
            this.indent = indent;
        }
    }

}
