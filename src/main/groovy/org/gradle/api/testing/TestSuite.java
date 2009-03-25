package org.gradle.api.testing;

import java.io.File;
import java.util.List;

/**
 * @author Tom Eyckmans
 */
public interface TestSuite {

    TestFramework getTestFramework();

    List<File> getTestClassDirectories();

}
