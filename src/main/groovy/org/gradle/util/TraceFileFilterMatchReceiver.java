package org.gradle.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Tom Eyckmans
 */
public class TraceFileFilterMatchReceiver implements FileFilterMatchReceiver {

    private static final Logger logger = LoggerFactory.getLogger(TraceFileFilterMatchReceiver.class);

    public void receive(File filterMatch, String filterMatchPath) {
        logger.debug("received match {} for file {}", filterMatchPath, filterMatch.getAbsolutePath());
    }
}
