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

import java.util.HashMap;
import java.util.Map;

// Complete list of Mime types is available at https://www.iana.org/assignments/media-types/application.csv.
public class HardCodedContentTypeFromResourceName
        implements ContentTypeFromResourceName {

    private final Map<String, String> map = new HashMap<>();

    public HardCodedContentTypeFromResourceName() {
        // From https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Complete_list_of_MIME_types
        map.put("aac", "audio/aac");
        map.put("abw", "application/x-abiword");
        map.put("arc", "application/x-freearc");
        map.put("avi", "video/x-msvideo");
        map.put("azw", "application/vnd.amazon.ebook");
        map.put("bin", "application/octet-stream");
        map.put("bmp", "image/bmp");
        map.put("bz", "application/x-bzip");
        map.put("bz2", "application/x-bzip2");
        map.put("csh", "application/x-csh");
        map.put("css", "text/css");
        map.put("csv", "text/csv");
        map.put("doc", "application/msword");
        map.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        map.put("eot", "application/vnd.ms-fontobject");
        map.put("epub", "application/epub+zip");
        map.put("gz", "application/gzip");
        map.put("gif", "image/gif");
        map.put("htm", "text/html");
        map.put("html", "text/html");
        map.put("ico", "image/vnd.microsoft.icon");
        map.put("ics", "text/calendar");
        map.put("jar", "application/java-archive");
        map.put("jpeg", "image/jpeg");
        map.put("jpg", "image/jpeg");
        // Mozilla list says text/javascript but that is now obsolete, and application/javascript is correct
        // https://tools.ietf.org/html/rfc4329#section-7
        map.put("js", "application/javascript");
        map.put("json", "application/json");
        map.put("jsonld", "application/ld+json");
        map.put("mid", "audio/midi");
        map.put("midi", "audio/midi");
        // Modified as per comment for "js"
        map.put("mjs", "application/javascript");
        map.put("mp3", "audio/mpeg");
        map.put("mpeg", "video/mpeg");
        map.put("mpkg", "application/vnd.apple.installer+xml");
        map.put("odp", "application/vnd.oasis.opendocument.presentation");
        map.put("ods", "application/vnd.oasis.opendocument.spreadsheet");
        map.put("odt", "application/vnd.oasis.opendocument.text");
        map.put("oga", "audio/ogg");
        map.put("ogv", "video/ogg");
        map.put("ogx", "application/ogg");
        map.put("otf", "font/otf");
        map.put("png", "image/png");
        map.put("pdf", "application/pdf");
        // With fix for typo in Mozilla list.
        map.put("php", "application/php");
        map.put("ppt", "application/vnd.ms-powerpoint");
        map.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        map.put("rar", "application/x-rar-compressed");
        map.put("rtf", "application/rtf");
        map.put("sh", "application/x-sh");
        map.put("svg", "image/svg+xml");
        map.put("swf", "application/x-shockwave-flash");
        map.put("tar", "application/x-tar");
        map.put("tif", "image/tiff");
        map.put("tiff", "image/tiff");
        map.put("ts", "video/mp2t");
        map.put("ttf", "font/ttf");
        map.put("txt", "text/plain");
        map.put("vsd", "application/vnd.visio");
        map.put("wav", "audio/wav");
        map.put("weba", "audio/webm");
        map.put("webm", "video/webm");
        map.put("webp", "image/webp");
        map.put("woff", "font/woff");
        map.put("woff2", "font/woff2");
        map.put("xhtml", "application/xhtml+xml");
        map.put("xls", "application/vnd.ms-excel");
        map.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        // Can be text/xml or application/xml depending on whether a casual user can read it.
        map.put("xml", "application/xml");
        map.put("xul", "application/vnd.mozilla.xul+xml");
        map.put("zip", "application/zip");
        // audio/3gpp if it doesn't contain video
        map.put("3gp", "video/3gpp");
        // audio/3gppw if it doesn't contain video
        map.put("3g2", "video/3gpp2");
        map.put("7z", "application/x-7z-compressed");

        // Camera raw extensions from https://en.wikipedia.org/wiki/Raw_image_format.
        // Pattern x-raw-manufacturer observed with Apache Tika.
        map.put("crw", "image/x-raw-canon");
        map.put("cr2", "image/x-canon-cr2");
        map.put("cr3", "image/x-canon-cr3");
        map.put("nef", "image/x-raw-nikon");
        map.put("nrw", "image/x-raw-nikon");
        map.put("rw2", "image/x-raw-panasonic");
        map.put("arw", "image/x-raw-sony");
        map.put("raf", "image/x-raw-fuji");

        // Hmm. IANA list says "application/mp4", "video/mp4" agrees with Tika.
        map.put("mp4", "video/mp4");

        // Additional values selected from IANA list.
        map.put("octet-stream", "application/octet-stream");
    }

    @Override
    public String determineContentType(String resourceName) {
        int lastDotIndex = resourceName.lastIndexOf(".");
        String extension = resourceName.substring(lastDotIndex + 1).toLowerCase();
        return map.get(extension);
    }

}
