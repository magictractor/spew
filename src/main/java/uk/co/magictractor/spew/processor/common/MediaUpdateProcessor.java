package uk.co.magictractor.spew.processor.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.processor.MediaProcessor;

public abstract class MediaUpdateProcessor implements MediaProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected Logger getLogger() {
        return logger;
    }

}
