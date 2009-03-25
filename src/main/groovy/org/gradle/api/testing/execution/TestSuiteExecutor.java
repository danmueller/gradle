package org.gradle.api.testing.execution;

import org.gradle.api.testing.TestSuite;

import java.util.List;

/**
 * @author Tom Eyckmans
 */
public interface TestSuiteExecutor {
    void execute(TestSuite testSuite);
}
