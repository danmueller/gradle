package org.gradle.api.testing.framework;

import org.gradle.api.testing.TestFrameworkObjectFactory;
import org.gradle.api.testing.TestFrameworkTestInfoWriter;
import org.gradle.api.testing.TestFrameworkTestInfoReader;

import java.io.BufferedWriter;
import java.io.BufferedReader;

/**
 * When a TestFramework doesn't need special treatement of TestInfo the TestFrameworkObjectFactory can extend this class.
 *
 * @author Tom Eyckmans
 */
public abstract class AbstractTestFrameworkObjectFactory implements TestFrameworkObjectFactory {
    public TestFrameworkTestInfoWriter createTestInfoWriter(BufferedWriter outputWriter) {
        if ( outputWriter == null ) throw new IllegalArgumentException("outputWriter is null!");

        return new DefaultTestInfoWriter(outputWriter);
    }

    public TestFrameworkTestInfoReader createTestInfoReader(BufferedReader inputReader) {
        if ( inputReader == null ) throw new IllegalArgumentException("inputReader is null!");

        return new DefaultTestInfoReader(inputReader);
    }
}
