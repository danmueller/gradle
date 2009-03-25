package org.gradle.api.testing.execution.control;

import org.gradle.api.GradleException;

import java.io.File;

/**
 * @author Tom Eyckmans
 */
public class DefaultUsedPortsFileProvider implements UsedPortsFileProvider {
    public File getUsedPortsFile() {
        final File userHomeDirectory = new File(System.getProperty("user.home"));
        final File gradleUserHomeDirectory = new File(userHomeDirectory, ".gradle");
        final File internalDirectory = new File(gradleUserHomeDirectory, "internal");
        final File internalTestingDirectory = new File(internalDirectory, "testing");
        if ( !internalTestingDirectory.exists() && !internalTestingDirectory.mkdirs() ) {
            throw new GradleException("failed to create directory " + internalTestingDirectory.getAbsolutePath());
        }
        return new File(internalTestingDirectory, "used.ports");
    }
}
