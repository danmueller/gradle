package org.gradle.api.testing.execution.control;

import java.io.File;

/**
 * @author Tom Eyckmans
 */
public interface UsedPortsFileProvider {
    File getUsedPortsFile();
}
