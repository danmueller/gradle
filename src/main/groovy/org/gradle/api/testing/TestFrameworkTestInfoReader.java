package org.gradle.api.testing;

import java.io.IOException;

/**
 * The TestInfo is written to disk, at some point this information needs to be read back into memory.
 * The TestInfo reader provides this functionality.
 *
 * @author Tom Eyckmans
 */
public interface TestFrameworkTestInfoReader {
    /**
     * Read one TestInfo from disk.
     *
     * @return The read TestInfo.
     * @throws IOException Is thrown when something goes wrong when reading from disk.
     */
    TestInfo read() throws IOException;
}
