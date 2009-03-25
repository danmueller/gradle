package org.gradle.api.testing;

import java.io.File;
import java.util.List;

/**
 * @author Tom Eyckmans
 */
public interface TestingObjectFactory {

    TestSuite createTestSuite(final TestFramework testFramework, final List<File> testClassDirectories, final List<File> testClasspath);

}
