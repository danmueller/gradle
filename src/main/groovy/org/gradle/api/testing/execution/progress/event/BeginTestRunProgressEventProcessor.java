package org.gradle.api.testing.execution.progress.event;

import org.gradle.api.testing.execution.progress.TestRunProgressEventProcessor;
import org.gradle.api.testing.execution.progress.TestRunProgressEvent;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * @author Tom Eyckmans
 */
public class BeginTestRunProgressEventProcessor implements TestRunProgressEventProcessor {
    private static final Logger logger = LoggerFactory.getLogger(BeginTestRunProgressEventProcessor.class);

    public void process(TestRunProgressEvent event) {
        logger.info("received test run begin progress event at {}", event.getCreateMillis());
    }

    public boolean isSupported(Class<? extends TestRunProgressEvent> eventClass) {
        return BeginTestRunProgressEvent.class == eventClass;
    }
}
