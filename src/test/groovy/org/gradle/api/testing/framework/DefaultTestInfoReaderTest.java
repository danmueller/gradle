package org.gradle.api.testing.framework;

import org.gradle.util.JUnit4GroovyMockery;
import org.gradle.api.testing.TestInfo;
import org.jmock.lib.legacy.ClassImposteriser;
import org.jmock.Expectations;
import org.junit.Test;
import org.junit.Before;

import java.io.BufferedReader;
import java.io.IOException;

import static junit.framework.Assert.*;

/**
 * @author Tom Eyckmans
 */
public class DefaultTestInfoReaderTest {
    protected JUnit4GroovyMockery context = new JUnit4GroovyMockery();

    private DefaultTestInfoReader testInfoReader;
    private BufferedReader inputReaderMock;

    @Before
    public void setUp() throws Exception
    {
        context.setImposteriser(ClassImposteriser.INSTANCE);

        inputReaderMock = context.mock(BufferedReader.class);

        testInfoReader = new DefaultTestInfoReader(inputReaderMock);
    }

    @Test
    public void testProperTestInfoLine() throws IOException {
        final String testClassName = "org.gradle.api.internal.artifacts.ivyservice.DefaultSettingsConverterTest";
        final String testInfoClassName = "org.gradle.external.junit.JUnitTestCaseTestInfo";
        final String properTestInfoLine = testClassName + ":" + testInfoClassName;

        context.checking(new Expectations(){{
            one(inputReaderMock).readLine(); will(returnValue(properTestInfoLine));
        }});

        final TestInfo readTestInfo = testInfoReader.read();

        assertNotNull(readTestInfo);
        assertEquals(testClassName, readTestInfo.getTestClassName());
        assertEquals(testInfoClassName, readTestInfo.getClass().getName());
    }
}
