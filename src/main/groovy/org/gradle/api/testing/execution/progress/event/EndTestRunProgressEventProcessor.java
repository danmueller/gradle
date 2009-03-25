package org.gradle.api.testing.execution.progress.event;

import org.gradle.api.testing.execution.progress.TestRunProgressEventProcessor;
import org.gradle.api.testing.execution.progress.TestRunProgressEvent;
import org.gradle.api.testing.execution.progress.TestRunProgressEventQueueConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tom Eyckmans
 */
public class EndTestRunProgressEventProcessor implements TestRunProgressEventProcessor {
    private static final Logger logger = LoggerFactory.getLogger(EndTestRunProgressEventProcessor.class);
    private final TestRunProgressEventQueueConsumer progressEventQueueConsumer;

    public EndTestRunProgressEventProcessor(TestRunProgressEventQueueConsumer progressEventQueueConsumer) {
        this.progressEventQueueConsumer = progressEventQueueConsumer;
    }

    public void process(TestRunProgressEvent event) {
        logger.info("received test run end progress event at {}", event.getCreateMillis());
    }

    public boolean isSupported(Class<? extends TestRunProgressEvent> eventClass) {
        return EndTestRunProgressEvent.class == eventClass;
    }
}
