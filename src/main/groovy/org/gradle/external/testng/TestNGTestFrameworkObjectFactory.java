package org.gradle.external.testng;

import org.gradle.api.testing.TestFrameworkDetector;
import org.gradle.api.testing.TestFrameworkRunner;
import org.gradle.api.testing.framework.AbstractTestFrameworkObjectFactory;

/**
 * @author Tom Eyckmans
 */
public class TestNGTestFrameworkObjectFactory extends AbstractTestFrameworkObjectFactory {


    public TestFrameworkDetector createDetector() {
        return new TestNGDetector();
    }

    public TestFrameworkRunner createRunner() {
        return new TestNGTestFrameworkRunner();
    }
}
