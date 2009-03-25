package org.gradle.api.testing.execution.progress;

import org.gradle.api.testing.execution.control.TestControlMessage;

/**
 * @author Tom Eyckmans
 */
public interface TestRunProgressEvent extends TestControlMessage {
    long getCreateMillis();
}
