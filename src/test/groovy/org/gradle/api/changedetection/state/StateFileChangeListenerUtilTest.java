package org.gradle.api.changedetection.state;

import org.gradle.util.JUnit4GroovyMockery;
import org.gradle.util.queues.BlockingQueueItemProducer;
import org.junit.Before;
import org.junit.Test;
import org.jmock.lib.legacy.ClassImposteriser;
import org.jmock.Expectations;

import java.io.File;

/**
 * @author Tom Eyckmans
 */
public class StateFileChangeListenerUtilTest {
    private final JUnit4GroovyMockery context = new JUnit4GroovyMockery();

    private BlockingQueueItemProducer<StateChangeEvent> changeProcessorEventProducerMock;
    private StateChangeEventFactory stateChangeEventFactoryMock;
    private File fileOrDirectoryMock;
    private StateFileItem oldStateFileItemMock;
    private StateFileItem newStateFileItemMock;
    private StateChangeEvent stateChangeEventMock;

    private StateFileChangeListenerUtil stateFileChangeListenerUtil;

    @Before
    public void setUp() {
        context.setImposteriser(ClassImposteriser.INSTANCE);

        changeProcessorEventProducerMock = context.mock(BlockingQueueItemProducer.class);
        stateChangeEventFactoryMock = context.mock(StateChangeEventFactory.class);
        fileOrDirectoryMock = context.mock(File.class);
        oldStateFileItemMock = context.mock(StateFileItem.class, "old");
        newStateFileItemMock = context.mock(StateFileItem.class, "new");
        stateChangeEventMock = context.mock(StateChangeEvent.class);

        stateFileChangeListenerUtil = new StateFileChangeListenerUtil(changeProcessorEventProducerMock, stateChangeEventFactoryMock);
    }

    /**
     * Verify that old/new state file items are correctly transfered to the state changed event for new files/directories.
     */
    @Test
    public void testProduceCreatedItemEvent() {
        context.checking(new Expectations(){{
            one(stateChangeEventFactoryMock).createStateChangeEvent(fileOrDirectoryMock, null, newStateFileItemMock);
            will(returnValue(stateChangeEventMock));
            one(changeProcessorEventProducerMock).produce(stateChangeEventMock);
        }});

        stateFileChangeListenerUtil.produceCreatedItemEvent(fileOrDirectoryMock, newStateFileItemMock);
    }

    /**
     * Verify that old/new state file items are correctly transfered to the state changed event for deleted files/directories.
     */
    @Test
    public void testProduceDeletedItemEvent() {
        context.checking(new Expectations(){{
            one(stateChangeEventFactoryMock).createStateChangeEvent(fileOrDirectoryMock, oldStateFileItemMock, null);
            will(returnValue(stateChangeEventMock));
            one(changeProcessorEventProducerMock).produce(stateChangeEventMock);
        }});

        stateFileChangeListenerUtil.produceDeletedItemEvent(fileOrDirectoryMock, oldStateFileItemMock);
    }

    /**
     * Verify that old/new state file items are correctly transfered to the state changed event for changed files/directories.
     */
    @Test
    public void testProduceChangeItemEvent() {
        context.checking(new Expectations(){{
            one(stateChangeEventFactoryMock).createStateChangeEvent(fileOrDirectoryMock, oldStateFileItemMock, newStateFileItemMock);
            will(returnValue(stateChangeEventMock));
            one(changeProcessorEventProducerMock).produce(stateChangeEventMock);
        }});

        stateFileChangeListenerUtil.produceChangedItemEvent(fileOrDirectoryMock, oldStateFileItemMock, newStateFileItemMock);
    }
}
