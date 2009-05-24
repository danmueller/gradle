package org.gradle.api.changedetection.state;

import org.gradle.api.changedetection.state.StateChangeEvent;
import org.gradle.util.queues.BlockingQueueItemProducer;

import java.io.File;

/**
 * @author Tom Eyckmans
 */
class StateFileChangeListenerUtil {

    private final BlockingQueueItemProducer<StateChangeEvent> changeProcessorEventProducer;
    private final StateChangeEventFactory stateChangeEventFactory;

    StateFileChangeListenerUtil(final BlockingQueueItemProducer<StateChangeEvent> changeProcessorEventProducer, final StateChangeEventFactory stateChangeEventFactory) {
        if ( changeProcessorEventProducer == null ) throw new IllegalArgumentException("changeProcessorEventProducer is null!");
        if ( stateChangeEventFactory == null ) throw new IllegalArgumentException("stateChangeEventFactory is null!");

        this.changeProcessorEventProducer = changeProcessorEventProducer;
        this.stateChangeEventFactory = stateChangeEventFactory;
    }

    BlockingQueueItemProducer<StateChangeEvent> getChangeProcessorEventProducer() {
        return changeProcessorEventProducer;
    }

    void produceCreatedItemEvent(final File fileOrDirectory, final StateFileItem newState) {
        if ( fileOrDirectory == null ) throw new IllegalArgumentException("fileOrDirectory is null!");
        if ( newState == null ) throw new IllegalArgumentException("newState is null!");

        changeProcessorEventProducer.produce(stateChangeEventFactory.createStateChangeEvent(fileOrDirectory, null, newState));
    }

    void produceChangedItemEvent(final File fileOrDirectory, final StateFileItem oldState, final StateFileItem newState) {
        if ( fileOrDirectory == null ) throw new IllegalArgumentException("fileOrDirectory is null!");
        if ( oldState == null ) throw new IllegalArgumentException("oldState is null!");
        if ( newState == null ) throw new IllegalArgumentException("newState is null!");

        changeProcessorEventProducer.produce(stateChangeEventFactory.createStateChangeEvent(fileOrDirectory, oldState, newState));
    }

    void produceDeletedItemEvent(final File fileOrDirectory, final StateFileItem oldState) {
        if ( fileOrDirectory == null ) throw new IllegalArgumentException("fileOrDirectory is null!");
        if ( oldState == null ) throw new IllegalArgumentException("oldState is null!");
        
        changeProcessorEventProducer.produce(stateChangeEventFactory.createStateChangeEvent(fileOrDirectory, oldState, null));
    }
}
