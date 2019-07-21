package uk.co.magictractor.spew.api;

import com.jayway.jsonpath.Configuration;

// https://stackoverflow.com/questions/34111276/jsonpath-with-jackson-or-gson
// https://github.com/json-path/JsonPath

// TODO! bin - Jayway / XML parsing shoudl be split out of this - responses should wrap underlying response (HttpClientResponse etc)
public class XxxSpewJaywayResponse /* implements SpewResponse */ {

    public XxxSpewJaywayResponse(String response, Configuration jsonConfiguration) {
        // ctx = JsonPath.parse(response, jsonConfiguration);
    }

}
