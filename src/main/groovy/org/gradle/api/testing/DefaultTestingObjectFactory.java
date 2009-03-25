package org.gradle.api.testing;

import java.io.File;
import java.util.List;

/**
 * @author Tom Eyckmans
 */
class DefaultTestingObjectFactory implements TestingObjectFactory {


    public TestSuite createTestSuite(TestFramework testFramework, List<File> testClassDirectories, List<File> testClasspath) {
        return new DefaultTestSuite(testFramework, testClassDirectories, testClasspath);
    }
}
