package org.gradle.api.testing.execution.control;

/**
 * @author Tom Eyckmans
 */
public interface ServerPortControl {

    int reserveFreeServerPort();

}
