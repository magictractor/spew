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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TagLoader which reads tag hierarchy and aliases from plain text config files
 * with hierarchy defined by indents.
 */
public class IndentedFileTagLoader implements TagLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndentedFileTagLoader.class);

    private final String resourceName;
    //private int depth;

    private TagType tagType;
    private Deque<TagAndIndent> tagsAndIndents = new ArrayDeque<>();

    private boolean hasTabs;
    private boolean hasSpaces;

    public IndentedFileTagLoader() {
        this("/tags.txt", 0);
    }

    private IndentedFileTagLoader(String resourceName, int depth) {
        this.resourceName = resourceName;
        //this.depth = depth;
    }

    @Override
    public void loadTags() {
        InputStream fileStream = getClass().getResourceAsStream(resourceName);
        if (fileStream == null) {
            LOGGER.info("resource not found: {}", resourceName);
        }

        BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8));
        fileReader.lines().forEach(this::handleLine);
    }

    private void handleLine(String line) {
        System.err.println(line);

        if (line.trim().isEmpty()) {
            return;
        }

        if (line.charAt(0) == '!') {
            handleNewRoot(line);
        }
        else if (line.charAt(0) == '+') {
            handleOtherFile(line);
        }
        else {
            handleChild(line);
        }
    }

    private void handleNewRoot(String line) {
        String tagTypeName = line.substring(1).trim();
        tagType = TagType.valueOf(tagTypeName);
        tagsAndIndents.clear();
    }

    private void handleOtherFile(String line) {
        throw new UnsupportedOperationException();
    }

    private void handleChild(String line) {
        if (tagType == null) {
            throw new IllegalStateException(
                "Missing tag type - tag types are on a line starting with an exclamation mark");
        }

        int indent = parseIndent(line);
        //        while (tagsAndIndents.peek().indent >= indent) {
        //            tagsAndIndents.pop();
        //        }
        for (int i = tagsAndIndents.size(); i > 0; i--) {
            if (tagsAndIndents.peek().indent >= indent) {
                tagsAndIndents.pop();
            }
            else {
                break;
            }
        }

        String tagName = line.trim();
        Tag tag;
        if (tagsAndIndents.isEmpty()) {
            tag = Tag.createRoot(tagType, tagName);
        }
        else {
            tag = Tag.createChild(tagsAndIndents.peek().tag, tagName);
        }

        tagsAndIndents.push(new TagAndIndent(tag, indent));
    }

    private int parseIndent(String line) {
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

        return i;
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
