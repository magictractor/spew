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
package uk.co.magictractor.spew.processor.common;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.common.Photo;
import uk.co.magictractor.spew.common.Tag;
import uk.co.magictractor.spew.common.TagType;
import uk.co.magictractor.spew.processor.Processor;

public class BotanicsAlbumProcessor implements Processor<Photo, MutablePhoto, PhotoProcessorContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotanicsAlbumProcessor.class);

    private static final TagType LOCATION_TAG_TYPE = TagType.valueOf("LOCATION");
    private static final Tag BOTANICS_TAG = Tag.fetchTag("RBGE");

    @Override
    public void process(MutablePhoto photo, PhotoProcessorContext context) {
        Tag locationTag = photo.getTagSet().getDeepestTag(LOCATION_TAG_TYPE);
        if (BOTANICS_TAG.equals(locationTag)) {
            addToBotanicsAlbum(photo, context);
        }
    }

    private void addToBotanicsAlbum(MutablePhoto photo, PhotoProcessorContext context) {
        String albumTitle = getAlbumTitle(photo);
        MutableAlbum album = context.getAlbum(albumTitle);
        album.addPhoto(photo);
        LOGGER.info("Botanics album '{}' contains photo {}", album.getTitle(), photo.getTitle());
    }

    private String getAlbumTitle(MutablePhoto photo) {
        LocalDate dateTaken = photo.getDateTaken();
        StringBuilder albumTitleBuilder = new StringBuilder();
        albumTitleBuilder.append("RBGE ");
        albumTitleBuilder.append(dateTaken.getYear());
        if (dateTaken.getMonthValue() < 10) {
            albumTitleBuilder.append('0');
        }
        albumTitleBuilder.append(dateTaken.getMonthValue());

        return albumTitleBuilder.toString();
    }

}
