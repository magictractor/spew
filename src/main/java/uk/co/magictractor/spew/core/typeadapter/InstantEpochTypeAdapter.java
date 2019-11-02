/**
 * Copyright 2019 Ken Dobson
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
package uk.co.magictractor.spew.core.typeadapter;

import java.time.Instant;
import java.util.function.Function;

import uk.co.magictractor.spew.util.ExceptionUtil;

/**
 *
 */
public class InstantEpochTypeAdapter implements SpewTypeAdapter<Instant> {

    private final Function<String, Instant> converter;

    public static InstantEpochTypeAdapter MILLIS = new InstantEpochTypeAdapter(
        InstantEpochTypeAdapter::convertEpochMillis);
    public static InstantEpochTypeAdapter SECONDS = new InstantEpochTypeAdapter(
        InstantEpochTypeAdapter::convertEpochSeconds);

    private InstantEpochTypeAdapter(Function<String, Instant> converter) {
        this.converter = converter;
    }

    @Override
    public Class<Instant> getType() {
        return Instant.class;
    }

    @Override
    public Instant fromString(String valueAsString) {
        return converter.apply(valueAsString);
    }

    @Override
    public String toString(Instant value) {
        throw ExceptionUtil.notYetImplemented();
    }

    private static Instant convertEpochMillis(String stringValue) {
        long millis = Long.parseLong(stringValue);
        return Instant.ofEpochMilli(millis);
    }

    private static Instant convertEpochSeconds(String stringValue) {
        long seconds = Long.parseLong(stringValue);
        return Instant.ofEpochSecond(seconds);
    }

}
