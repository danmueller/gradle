package org.gradle.api.testing.framework;

import org.gradle.api.testing.TestFrameworkTestInfoWriter;
import org.gradle.api.testing.TestInfo;

import java.io.IOException;
import java.io.BufferedWriter;

/**
 * When no special treatement is needed by a TestFramework to store TestInfo this default TestInfo writer can be used.
 *
 * @author Tom Eyckmans
 */
public class DefaultTestInfoWriter implements TestFrameworkTestInfoWriter {
    private static final char SEPARATOR = ':';

    private final BufferedWriter outputWriter;

    public DefaultTestInfoWriter(BufferedWriter outputWriter) {
        if ( outputWriter == null ) throw new IllegalArgumentException("outputWriter is null!");

        this.outputWriter = outputWriter;
    }

    /**
     * Stores the classname of the test class and the class of the TestInfo object separated by a ':'.
     *
     * @param testInfo The TestInfo to store.
     * @throws IOException
     */
    public void write(TestInfo testInfo) throws IOException {
        if ( testInfo == null ) throw new IllegalArgumentException("testInfo is null!");

        final String testClassName = testInfo.getTestClassName();
        final String testInfoClassName = testInfo.getClass().getName();

        outputWriter.write(testClassName);
        outputWriter.write(SEPARATOR);
        outputWriter.write(testInfoClassName);
        outputWriter.newLine();
    }
}
