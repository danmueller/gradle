package org.gradle.api.testing.execution.progress;

/**
 * @author Tom Eyckmans
 */
public interface TestRunProgressEventProcessor {
    void process(TestRunProgressEvent event);

    boolean isSupported(Class<? extends TestRunProgressEvent> eventClass);
}
