package uk.co.magictractor.spew.api;

import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;

public interface SpewConnection {

    // TODO! SpewResponse rather than SpewParsedResponse here?
    SpewParsedResponse request(SpewRequest apiRequest);

}
