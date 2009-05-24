package org.gradle.api.changedetection.state;

import org.gradle.api.io.IoFactory;
import org.gradle.api.GradleException;
import org.gradle.util.JUnit4GroovyMockery;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.jmock.lib.legacy.ClassImposteriser;
import org.jmock.Expectations;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;

/**
 * @author Tom Eyckmans
 */
public class StateFileReaderTest {
    private final JUnit4GroovyMockery context = new JUnit4GroovyMockery();
    private final String okKey = "okKey";
    private final String okDigest = "okDigest";

    private IoFactory ioFactoryMock;
    private File testStateFileMock;
    private BufferedReader stateFileBufferedReaderMock;
    private StateFileReader stateFileReader;

    @Before
    public void setUp() throws IOException {
        context.setImposteriser(ClassImposteriser.INSTANCE);

        ioFactoryMock = context.mock(IoFactory.class);
        testStateFileMock = context.mock(File.class);
        stateFileBufferedReaderMock = context.mock(BufferedReader.class);

        stateFileReader = new StateFileReader(ioFactoryMock, testStateFileMock);
    }

    /**
     * Verify that a state file reader doesn't try to create a buffered reader for the state file when the state file doesn't exists,
     * and check that the state file reader is in 'simulation' mode after prepare.
     *
     * @throws IOException
     */
    @Test
    public void testPrepareForReadStateFileDoesNotExist() throws IOException {
        context.checking(new Expectations(){{
            one(testStateFileMock).exists();will(returnValue(false));
        }});

        stateFileReader.prepareForRead();

        assertTrue(stateFileReader.isSimulating());

        stateFileReader.lastStateFileItemRead();
    }

    /**
     * Verify that a state file reader creates a buffered reader for the state file when the state file exists,
     * and is not in simulation mode after prepare.
     *
     * @throws IOException
     */
    @Test
    public void testPrepareForReadStateFileExists() throws IOException {
        context.checking(new Expectations(){{
            one(testStateFileMock).exists();will(returnValue(true));
            one(testStateFileMock).isFile();will(returnValue(true));
            one(ioFactoryMock).createBufferedReader(testStateFileMock);will(returnValue(stateFileBufferedReaderMock));
        }});

        stateFileReader.prepareForRead();

        assertFalse(stateFileReader.isSimulating());

        context.checking(new Expectations(){{
            one(stateFileBufferedReaderMock).close();
        }});

        stateFileReader.lastStateFileItemRead();
    }

    /**
     * Verify that when an IOException is thrown by the IOFactory it is thrown from the prepare method.
     *
     * @throws IOException
     */
    @Test ( expected = IOException.class )
    public void testPrepareForReadStateIOException() throws IOException {
        context.checking(new Expectations(){{
            one(testStateFileMock).exists();will(returnValue(true));
            one(testStateFileMock).isFile();will(returnValue(true));
            one(ioFactoryMock).createBufferedReader(testStateFileMock);will(throwException(new IOException("failed to create buffered state file reader")));
        }});

        stateFileReader.prepareForRead();
    }

    /**
     * Verify that when calling readStateFileItem on an unprepared state file reader throws an IllegalStateException.
     *
     * @throws IOException
     */
    @Test ( expected = IllegalStateException.class )
    public void testNotPreparedReadStateFileItem() throws IOException {
        stateFileReader.readStateFileItem();
    }

    /**
     * Verify that when calling readStateFileItem on a 'simulating' state file reader no methods are called on the buffered reader,
     * and that a null state file item is returned.
     *
     * @throws IOException
     */
    @Test
    public void testSimulatingReadStateFileItem() throws IOException {
        testPrepareForReadStateFileDoesNotExist();

        final StateFileItem stateFileItem = stateFileReader.readStateFileItem();

        assertNull(stateFileItem);
    }

    /**
     * Verify that when calling readStateFileItem on a not simulating state file reader 2 lines are read from
     * the buffered reader on the state file. And that the key and digest lines are correctly transfered to the
     * state file item object.
     *
     * @throws IOException
     */
    @Test
    public void testNotSimulatingReadStateFileItem() throws IOException {
        testPrepareForReadStateFileExists();

        context.checking(new Expectations(){{
            one(stateFileBufferedReaderMock).readLine();will(returnValue(okKey));
            one(stateFileBufferedReaderMock).readLine();will(returnValue(okDigest));
        }});

        final StateFileItem stateFileItem = stateFileReader.readStateFileItem();

        assertNotNull(stateFileItem);
        assertEquals(okKey, stateFileItem.getKey());
        assertEquals(okDigest, stateFileItem.getDigest());
    }

    /**
     * Verify that a null state file item is returned when there are no more lines to read from the state
     * file buffered reader.
     *
     * @throws IOException
     */
    @Test
    public void testStateFileExhaustedReadStateFileItem() throws IOException {
        testPrepareForReadStateFileExists();

        context.checking(new Expectations(){{
            one(stateFileBufferedReaderMock).readLine();will(returnValue(null));
        }});

        final StateFileItem stateFileItem = stateFileReader.readStateFileItem();

        assertNull(stateFileItem);
    }

    /**
     * Verify that an exception is thrown when the state file is not valid.
     *
     * @throws IOException
     */
    @Test ( expected = GradleException.class )
    public void testInvalidStateFileReadStatFileItem() throws IOException {
        testPrepareForReadStateFileExists();

        context.checking(new Expectations(){{
            one(stateFileBufferedReaderMock).readLine();will(returnValue(okKey));
            one(stateFileBufferedReaderMock).readLine();will(returnValue(null));
        }});

        stateFileReader.readStateFileItem();
    }
}
