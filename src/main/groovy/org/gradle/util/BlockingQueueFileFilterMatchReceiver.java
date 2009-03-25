package org.gradle.util;

import java.io.File;

/**
 * @author Tom Eyckmans
 */
public class BlockingQueueFileFilterMatchReceiver implements FileFilterMatchReceiver {
    private final BlockingQueueItemProducer<File> filterMatches;

    public BlockingQueueFileFilterMatchReceiver(BlockingQueueItemProducer<File> filterMatches) {
        if ( filterMatches == null ) throw new IllegalArgumentException("filterMatches == null!");
        this.filterMatches = filterMatches;
    }

    public void receive(File filterMatch, String filterMatchPath) {
        filterMatches.produce(filterMatch);
    }
}
