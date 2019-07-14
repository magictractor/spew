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

import java.util.ArrayList;
import java.util.List;

import uk.co.magictractor.spew.common.Album;

public class MutableAlbum implements Album {

    /**
     * Unlike MutablePhoto, MutableAlbum is typically built without reference to
     * an underlying Album POJO. The underlying Album is generally only read
     * from the service provider when Album changes are persisted.
     */
    private Album underlyingAlbum;

    private String title;
    private List<MutablePhoto> photos = new ArrayList<>();

    public MutableAlbum(String title) {
        this.title = title;
    }

    public void setUnderlyingAlbum(Album underlyingAlbum) {
        if (this.underlyingAlbum != null) {
            throw new IllegalStateException("Underlying album has already been set");
        }
        this.underlyingAlbum = underlyingAlbum;
    }

    public Album getUnderlyingAlbum() {
        return underlyingAlbum;
    }

    public List<MutablePhoto> getPhotos() {
        return photos;
    }

    @Override
    public String getServiceProviderId() {
        return underlyingAlbum == null ? null : underlyingAlbum.getServiceProviderId();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getAlbumUrl() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public String getCoverPhotoBaseUrl() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public int getPhotoCount() {
        return photos.size();
    }

    public void addPhoto(MutablePhoto photo) {
        photos.add(photo);
    }

}
