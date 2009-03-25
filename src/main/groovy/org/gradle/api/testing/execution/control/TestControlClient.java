package org.gradle.api.testing.execution.control;

/**
 * @author Tom Eyckmans
 */
public interface TestControlClient {

    void reportBegin();

    void reportEnd();


//    TestAction getNextAction();
}
