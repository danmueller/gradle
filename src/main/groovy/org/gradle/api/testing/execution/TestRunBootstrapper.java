package org.gradle.api.testing.execution;

import org.gradle.api.testing.*;
import org.gradle.api.GradleException;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author Tom Eyckmans
 */
public class TestRunBootstrapper {

    private final DefaultTestSuiteExecutor executor;

    TestRunBootstrapper(DefaultTestSuiteExecutor executor) {
        if ( executor == null ) throw new IllegalArgumentException("executor == null!");

        this.executor = executor;
    }

    void bootstrap(TestSuite testSuite) {
        final TestFramework testFramework = testSuite.getTestFramework();
        final TestFrameworkObjectFactory objectFactory = testFramework.getObjectFactory();

        // detect & store tests with their type
        final File testInfoOutputFile = executor.getTestsFile(testFramework);
        BufferedWriter testInfoOutputWriter = null;
        try {
            testInfoOutputWriter = new BufferedWriter(new FileWriter(testInfoOutputFile));

            final List<File> testClassDirectories = testSuite.getTestClassDirectories();
            final TestFrameworkDetector detector = objectFactory.createDetector();
            final TestFrameworkTestInfoWriter testInfoWriter = objectFactory.createTestInfoWriter(testInfoOutputWriter);

            detectTests(testClassDirectories, testSuite, detector, testInfoWriter);

            testInfoOutputWriter.flush();
        }
        catch ( IOException e ) {
            throw new GradleException("failed to detect & store the tests for test framework " + testFramework.getName());
        }
        finally {
            IOUtils.closeQuietly(testInfoOutputWriter);
        }
    }

    void detectTests(List<File> testClassDirectories, TestSuite testSuite, TestFrameworkDetector testFrameworkDetector, TestFrameworkTestInfoWriter testInfoWriter) throws IOException {
        for ( File testClassDirectory : testClassDirectories ) {
            consumeTestClassDirectory(testClassDirectory, testSuite, testFrameworkDetector, testInfoWriter);
        }
    }

    void consumeTestClassDirectory(File testClassDirectory, TestSuite testSuite, TestFrameworkDetector testFrameworkDetector, TestFrameworkTestInfoWriter testInfoWriter) throws IOException {
        final File[] subFiles = testClassDirectory.listFiles();
        if ( subFiles != null && subFiles.length != 0 ) {
            for ( final File subFile : subFiles ) {
                if ( subFile.isDirectory() ) {
                    consumeTestClassDirectory(subFile, testSuite, testFrameworkDetector, testInfoWriter); // recurse into sub directories
                }
                else if (subFile.isFile()) {
                    final String subFileName = subFile.getName();
                    if ( subFileName.endsWith(".class") ) {
                        final TestInfo testInfo = testFrameworkDetector.isTest(testSuite, subFile);
                        if ( testInfo != null ) {
                            testInfoWriter.write(testInfo);
                        }
                    }
                    // else not a classfile
                }
                // else not directory or file
            }
        }
        // else empty package directory
    }
}
