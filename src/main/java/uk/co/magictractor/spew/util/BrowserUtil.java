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
package uk.co.magictractor.spew.util;

import java.awt.Desktop;
import java.net.URI;

/**
 * Utility for working with the default browser to open
 */
public final class BrowserUtil {

    private BrowserUtil() {
    }

    // https://stackoverflow.com/questions/5226212/how-to-open-the-default-webbrowser-using-java
    public static void openBrowserTab(String uri) {
        if (Desktop.isDesktopSupported()) {
            ExceptionUtil.call(() -> Desktop.getDesktop().browse(new URI(uri)));
        }
        else {
            throw new UnsupportedOperationException("TODO");
        }
    }

}
