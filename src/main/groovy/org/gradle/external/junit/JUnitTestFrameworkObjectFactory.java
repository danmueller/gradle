package org.gradle.external.junit;

import org.gradle.api.testing.*;
import org.gradle.api.testing.framework.AbstractTestFrameworkObjectFactory;

/**
 * @author Tom Eyckmans
 */
public class JUnitTestFrameworkObjectFactory extends AbstractTestFrameworkObjectFactory {
    public TestFrameworkDetector createDetector() {
        return new JUnitDetector();
    }

    public TestFrameworkRunner createRunner() {
        return new JUnitTestFrameworkRunner();
    }
}
