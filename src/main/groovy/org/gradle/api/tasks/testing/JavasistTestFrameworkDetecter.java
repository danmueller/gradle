package org.gradle.api.tasks.testing;

import java.io.IOException;
import java.io.File;
import java.util.Arrays;

import org.gradle.util.Clock;
import org.gradle.api.testing.TestFramework;
import org.gradle.api.testing.TestSuite;
import org.gradle.api.testing.DefaultTestSuite;
import org.gradle.api.testing.execution.DefaultTestSuiteExecutor;
import org.gradle.api.testing.execution.TestSuiteExecutor;
import org.gradle.external.junit.JUnitTestFramework;

/**
 * @author Tom Eyckmans
 */
public class JavasistTestFrameworkDetecter {

    public static void main(String[] args) throws IOException {

//        JavasistTestFrameworkDetecter testFrameworkDetecter = new JavasistTestFrameworkDetecter(new File("/home/teyckmans/lib/org/gradle/git/gradle/out/production/gradle-javadoc"));
        final TestFramework junitTestFramework = new JUnitTestFramework();

        final TestSuite testSuite = new DefaultTestSuite(junitTestFramework, Arrays.asList(new File("/home/teyckmans/lib/org/gradle/git/gradle/build/test-classes")), null);

        final TestSuiteExecutor testSuiteExecutor = new DefaultTestSuiteExecutor();
        
        Clock c = new Clock();

        testSuiteExecutor.execute(testSuite);

        System.out.println("time taken: " + c.getTime());

//        final Map<TestFramework, List<File>> testClassFiles = testContext.getTestClassFiles();
//        for ( final TestFramework testFramework : testClassFiles.keySet() ) {
//            System.out.println("found #"+testClassFiles.get(testFramework).size()+" test classes for test framework " + testFramework.getName() );
//        }
    }

}
