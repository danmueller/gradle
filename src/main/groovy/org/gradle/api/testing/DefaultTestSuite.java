package org.gradle.api.testing;

import java.io.File;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;

/**
 * @author Tom Eyckmans
 */
public class DefaultTestSuite implements TestSuite {

    private final TestFramework testFramework;
    private final List<File> testClassDirectories;
    private final List<File> testClasspath;

    public DefaultTestSuite(TestFramework testFramework, List<File> testClassDirectories, List<File> testClasspath) {
        if ( testFramework == null ) throw new IllegalArgumentException("testFramework == null!");
        if ( testClassDirectories == null ) throw new IllegalArgumentException("testClassDirectories == null!");
        if ( testClassDirectories.size() == 0 ) throw new IllegalArgumentException("testClassDirectories is empty!");
        if ( testClasspath == null ) throw new IllegalArgumentException("testClasspath == null!");
        this.testFramework = testFramework;
        this.testClassDirectories = new ArrayList<File>(testClassDirectories);
        this.testClasspath = new ArrayList<File>(testClasspath);
    }

    public TestFramework getTestFramework() {
        return testFramework;
    }

    public List<File> getTestClassDirectories() {
        return Collections.unmodifiableList(testClassDirectories);
    }

    public List<File> getTestClasspath() {
        return Collections.unmodifiableList(testClasspath);
    }
}
