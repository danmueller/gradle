package org.gradle.api.testing.execution.progress;

/**
 * @author Tom Eyckmans
 */
public interface TestRunProgressEventDispatcher {

    <T extends TestRunProgressEvent> void registerEventProcessor(Class<T> eventClass, TestRunProgressEventProcessor eventProcessor);
    
    void dispatch(TestRunProgressEvent event);

}
