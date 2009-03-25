package org.gradle.api.testing;

import java.io.IOException;

/**
 * A TestFramework TestInfo writer is used to write the tests that need to be executed to disk.
 *
 * This is done to prevent memory issues on very large projects. 
 *
 * @author Tom Eyckmans
 */
public interface TestFrameworkTestInfoWriter {
    /**
     * Store the TestInfo on disk.
     *
     * @param testInfo The TestInfo to store.
     * @throws IOException In case the write failed.
     */
    void write(TestInfo testInfo) throws IOException;
}
