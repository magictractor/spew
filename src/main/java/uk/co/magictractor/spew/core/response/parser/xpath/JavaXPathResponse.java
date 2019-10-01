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
package uk.co.magictractor.spew.core.response.parser.xpath;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import com.google.common.base.MoreObjects;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.core.response.parser.AbstractSpewParsedResponse;
import uk.co.magictractor.spew.core.response.parser.ObjectCentricSpewParsedResponse;
import uk.co.magictractor.spew.util.ExceptionUtil;
import uk.co.magictractor.spew.util.HttpMessageUtil;

/**
 *
 */
public class JavaXPathResponse
        extends AbstractSpewParsedResponse
        implements ObjectCentricSpewParsedResponse {

    private final XPath xpath;
    // private final InputSource xml;
    private final Document xml;

    /**
     * Default visibility because instances should only be created via
     * JaywayResponseParserInit.
     */
    JavaXPathResponse(SpewApplication<?> application, SpewHttpResponse response) {
        super(response);

        BufferedReader bodyReader = HttpMessageUtil.createBodyReader(response);
        xpath = XPathFactory.newInstance().newXPath();
        //String expression = "/widgets/widget";

        //xml = new InputSource(bodyReader);

        xml = ExceptionUtil.call(() -> readXml(response));

        //NodeList nodes = (NodeList) xpath.evaluate(expression, inputSource, XPathConstants.NODESET);
    }

    private Document readXml(SpewHttpResponse response) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(HttpMessageUtil.createBodyInputStream(response));
    }

    @Override
    public <T> T getObject(String key, Class<T> type) {
        return ExceptionUtil.call(() -> xpath.evaluateExpression(mapKey(key), xml, type));
        // throw ExceptionUtil.notYetImplemented();
    }

    @Override
    public <T> List<T> getList(String key, Class<T> elementType) {
        throw ExceptionUtil.notYetImplemented();
    }

    // map key from JsonPath to XPath
    // https://goessner.net/articles/JsonPath/
    private String mapKey(String key) {
        String mapped;
        if (key.startsWith("$.")) {
            mapped = "/" + key.substring(2).replace(".", "/");
        }
        else if (!key.contains("/")) {
            // mapped = "//rsp/@" + key; // gets stat, not err & msg
            // mapped = "/rsp/@" + key; // gets stat, not err & msg
            mapped = "/rsp/@" + key;
        }
        // temp, while I play...
        else if (key.startsWith("/")) {
            mapped = key;
        }
        else {
            throw new IllegalArgumentException("Code needs modified to map key " + key);
        }

        getLogger().info("JsonPath mapped to XPath: {} -> {}", key, mapped);

        return mapped;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                //.add("ctx.json", ctx.json())
                .toString();
    }

}
