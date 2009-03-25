package org.gradle.api.testing.execution.progress;

import org.gradle.util.AbstractBlockingQueueItemConsumer;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Tom Eyckmans
 */
public class TestRunProgressEventQueueConsumer extends AbstractBlockingQueueItemConsumer<TestRunProgressEvent> {
    private static final Logger logger = LoggerFactory.getLogger(TestRunProgressEventQueueConsumer.class);
    private final TestRunProgressEventDispatcher eventDispatcher;

    public TestRunProgressEventQueueConsumer(BlockingQueue<TestRunProgressEvent> testRunProgressEvents, TestRunProgressEventDispatcher eventDispatcher) {
        super(testRunProgressEvents, 100L, TimeUnit.MILLISECONDS);
        if ( eventDispatcher == null ) throw new IllegalArgumentException("eventDispatcher == null!");
        this.eventDispatcher = eventDispatcher;
    }

    protected boolean consume(TestRunProgressEvent testRunProgressEvent) {
        try {
            eventDispatcher.dispatch(testRunProgressEvent);
        }
        catch ( Throwable t ) {
            logger.error("Failed to process testRunProgressEvent {}", testRunProgressEvent);
        }

        return true;
    }
}
