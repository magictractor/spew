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
package uk.co.magictractor.spew.core.contenttype;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

public abstract class AbstractTestContentTypeFromResourceName {

    private static final List<String> RAW_EXTENSIONS = Arrays.asList("crw", "cr2", "cr3", "nef", "nrw", "rw2", "arw",
        "raf");
    private static List<String> UNSUPPORTED_EXTENSIONS = new ArrayList<>();

    protected abstract String determineContentType(String resourceName);

    @AfterAll
    public static void tearDownUnsupported() {
        UNSUPPORTED_EXTENSIONS.clear();
    }

    protected static void unsupported(String... extensions) {
        for (String extension : extensions) {
            UNSUPPORTED_EXTENSIONS.add(extension);
        }
    }

    protected static void unsupportedRaw() {
        UNSUPPORTED_EXTENSIONS.addAll(RAW_EXTENSIONS);
    }

    protected boolean isSupportedExtension(String extension) {
        return !UNSUPPORTED_EXTENSIONS.contains(extension);
    }

    protected String unknownContentType() {
        return "application/octet-stream";
    }

    @Test
    public void testHtml() {
        check("main.html", "text/html");
    }

    @Test
    public void testHtm() {
        check("main.htm", "text/html");
    }

    @Test
    public void testCss() {
        check("styles.css", "text/css");
    }

    @Test
    public void testJs() {
        check("script.js", "application/javascript");
    }

    @Test
    public void testTif() {
        check("picture.tif", "image/tiff");
    }

    @Test
    public void testTiff() {
        check("picture.tiff", "image/tiff");
    }

    @Test
    public void testPng() {
        check("picture.png", "image/png");
    }

    @Test
    public void testJpg() {
        check("picture.jpg", "image/jpeg");
    }

    @Test
    public void testJpeg() {
        check("picture.jpeg", "image/jpeg");
    }

    @Test
    public void testCrw() {
        check("photo.crw", "image/x-raw-canon");
    }

    @Test
    public void testCr2() {
        check("photo.cr2", "image/x-canon-cr2");
    }

    @Test
    public void testCr3() {
        check("photo.cr3", "image/x-canon-cr3");
    }

    @Test
    public void testNef() {
        check("photo.nef", "image/x-raw-nikon");
    }

    @Test
    public void testNrw() {
        check("photo.nrw", "image/x-raw-nikon");
    }

    @Test
    public void testRw2() {
        check("photo.rw2", "image/x-raw-panasonic");
    }

    @Test
    public void testArw() {
        check("photo.arw", "image/x-raw-sony");
    }

    @Test
    public void testRaf() {
        check("photo.raf", "image/x-raw-fuji");
    }

    @Test
    public void testMp4() {
        check("video.mp4", "video/mp4");
    }

    @Test
    public void testMp3() {
        check("music.mp3", "audio/mpeg");
    }

    @Test
    public void testOga() {
        check("music.oga", "audio/ogg");
    }

    @Test
    public void testDoc() {
        check("cv.doc", "application/msword");
    }

    @Test
    public void testPdf() {
        check("cv.pdf", "application/pdf");
    }

    @Test
    public void testTxt() {
        check("cv.txt", "text/plain");
    }

    @Test
    public void testXml() {
        check("config.xml", "application/xml");
    }

    @Test
    public void testIco() {
        check("favicon.ico", "image/vnd.microsoft.icon");
    }

    @Test
    public void testTtf() {
        check("font.ttf", "font/ttf");
    }

    @Test
    public void testZip() {
        check("archive.zip", "application/zip");
    }

    @Test
    public void test7z() {
        check("archive.7z", "application/x-7z-compressed");
    }

    /** Bin appears in the Mozilla list but not the IANA list. */
    @Test
    public void testBin() {
        check("binary.bin", "application/octet-stream");
    }

    /**
     * The "octet-stream" suffix appears in the complete list at
     * https://www.iana.org/assignments/media-types/application.csv.
     */
    @Test
    public void testOctetStream() {
        check("binary.octet-stream", "application/octet-stream");
    }

    @Test
    public void testUppercase() {
        check("PICTURE.JPG", "image/jpeg");
    }

    /**
     * Very obscure content type - reasonable that many implementations won't
     * support it. Only the Tika implementation handles this.
     */
    @Test
    public void testObscure() {
        check("obscure.fits", "application/fits");
    }

    @Test
    public void testUnknown() {
        check("unknown.mcwomble", unknownContentType());
    }

    @Test
    public void testNoExtension() {
        check("noextension", unknownContentType());
    }

    protected void check(String resourceName, String expectedContentType) {
        String actual = determineContentType(resourceName);

        int lastDotIndex = resourceName.lastIndexOf(".");
        String extension = resourceName.substring(lastDotIndex + 1).toLowerCase();
        String expected;
        if (isSupportedExtension(extension)) {
            expected = expectedContentType;
        }
        else {
            expected = unknownContentType();
        }
        // If not supported, test against null other than possible fallback "application/octet-stream"
        assertThat(actual).isEqualTo(expected);
    }

}
