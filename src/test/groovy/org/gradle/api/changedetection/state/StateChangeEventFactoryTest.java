package org.gradle.api.changedetection.state;

import org.gradle.util.JUnit4GroovyMockery;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.jmock.lib.legacy.ClassImposteriser;

import java.io.File;

/**
 * @author Tom Eyckmans
 */
public class StateChangeEventFactoryTest {

    private final JUnit4GroovyMockery context = new JUnit4GroovyMockery();

    private File fileOrDirectoryMock;
    private StateFileItem oldStateFileItemMock;
    private StateFileItem newStateFileItemMock;

    private StateChangeEventFactory stateChangeEventFactory;

    @Before
    public void setUp() {
        context.setImposteriser(ClassImposteriser.INSTANCE);

        fileOrDirectoryMock = context.mock(File.class);
        oldStateFileItemMock = context.mock(StateFileItem.class, "old");
        newStateFileItemMock = context.mock(StateFileItem.class, "new");

        stateChangeEventFactory = new StateChangeEventFactory();
    }

    /**
     * Verify that old/new state are passed correctly into the state change event. 
     */
    @Test
    public void testCreateStateChangeEvent() {
        final StateChangeEvent stateChangeEvent = stateChangeEventFactory.createStateChangeEvent(
                fileOrDirectoryMock,
                oldStateFileItemMock,
                newStateFileItemMock
        );

        assertNotNull(stateChangeEvent);
        assertEquals(fileOrDirectoryMock, stateChangeEvent.getFileOrDirectory());
        assertEquals(oldStateFileItemMock, stateChangeEvent.getOldState());
        assertEquals(newStateFileItemMock, stateChangeEvent.getNewState());
    }
}
