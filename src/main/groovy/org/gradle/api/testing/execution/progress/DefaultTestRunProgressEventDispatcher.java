package org.gradle.api.testing.execution.progress;

import org.gradle.api.GradleException;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Tom Eyckmans
 */
public class DefaultTestRunProgressEventDispatcher implements TestRunProgressEventDispatcher {

    private final Map<Class<? extends TestRunProgressEvent>, TestRunProgressEventProcessor> eventProcessors = new HashMap<Class<? extends TestRunProgressEvent>, TestRunProgressEventProcessor>();

    public DefaultTestRunProgressEventDispatcher() {

    }

    public <T extends TestRunProgressEvent> void registerEventProcessor(Class<T> eventClass, TestRunProgressEventProcessor eventProcessor) {
        if ( eventClass == null ) throw new IllegalArgumentException("eventClass == null!");
        if ( eventProcessor == null ) throw new IllegalArgumentException("eventProcessor == null!");
        if ( !eventProcessor.isSupported(eventClass) ) throw new IllegalArgumentException("eventProcessor "+eventProcessor.getClass().getName()+" doesn't support eventClass " + eventClass.getName());

        eventProcessors.put(eventClass, eventProcessor);
    }

    public void dispatch(TestRunProgressEvent event) {
        if ( event == null ) throw new IllegalArgumentException("event == null!");

        final Class<? extends TestRunProgressEvent> eventClass = event.getClass();
        final TestRunProgressEventProcessor eventProcessor = eventProcessors.get(eventClass);

        if ( eventProcessor == null ) throw new GradleException("no eventProcessor registered for eventClass " + eventClass.getName());

        eventProcessor.process(event);
    }
}
