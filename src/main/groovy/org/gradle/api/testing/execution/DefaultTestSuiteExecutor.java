package org.gradle.api.testing.execution;

import org.gradle.api.testing.*;

import java.io.File;
import java.io.IOException;

/**
 * @author Tom Eyckmans
 */
public class DefaultTestSuiteExecutor implements TestSuiteExecutor {
    private final File internalTestingDir;

    public DefaultTestSuiteExecutor() throws IOException {
        this(new File(".gradle/testing"));
    }

    public DefaultTestSuiteExecutor(File internalTestingDir) throws IOException {
        this.internalTestingDir = internalTestingDir;
        if ( !internalTestingDir.exists() ) {
            if ( !internalTestingDir.mkdirs() )
                throw new IOException("failed to create " + internalTestingDir);
        }
    }

    public void execute(TestSuite testSuite) {

        final TestRunBootstrapper bootstrapper = new TestRunBootstrapper(this);

        bootstrapper.bootstrap(testSuite);

        final TestRunControl control = new TestRunControl(this);

        // execute the tests / generate reports
        control.startupTestServer(testSuite);
        control.startupTestClient(testSuite);
        control.runTests(testSuite);
        control.stopTestClient(testSuite);
        control.stopTestServer(testSuite);
    }

    File getTestsFile(TestFramework testFramework) {
        return new File(internalTestingDir, testFramework.getId() + ".tests");
    }
}
