package org.gradle.api.testing.execution;

import org.gradle.api.testing.TestSuite;
import org.gradle.api.testing.TestFramework;
import org.gradle.api.testing.TestFrameworkTestInfoReader;
import org.gradle.api.testing.TestFrameworkObjectFactory;
import org.gradle.api.testing.execution.progress.TestRunProgressEvent;
import org.gradle.api.testing.execution.control.transport.IoAcceptorProvider;
import org.gradle.api.testing.execution.control.transport.ExternalIoAcceptorProvider;
import org.gradle.api.testing.execution.control.TestControlServer;
import org.gradle.api.testing.execution.control.DefaultTestControlServer;
import org.gradle.api.GradleException;
import org.gradle.util.*;
import org.gradle.util.exec.ExecHandle;
import org.gradle.util.exec.ExecHandleBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.util.JavaEnvUtils;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;

/**
 * @author Tom Eyckmans
 */
public class TestRunControl {
    private final DefaultTestSuiteExecutor executor;

    private BlockingQueue<TestRunProgressEvent> progressEventQueue = null;
    private IoAcceptorProvider ioAcceptorProvider = null;
    private BufferedReader testInfoInputFileReader = null;
    private TestControlServer controlServer = null;
    private int serverPort = -1;

    private AbstractBlockingQueueItemConsumer<TestRunProgressEvent> testRunProgressEventConsumer;

    private ExecHandle clientVmHandle;

    public TestRunControl(DefaultTestSuiteExecutor executor) {
        if ( executor == null ) throw new IllegalArgumentException("executor is null!");
        this.executor = executor;
    }

    void startupTestServer(TestSuite testSuite) {
        final TestFramework testFramework = testSuite.getTestFramework();
        final TestFrameworkObjectFactory objectFactory = testFramework.getObjectFactory();

        final File testInfoInputFile = executor.getTestsFile(testFramework);

        try {
            testInfoInputFileReader = new BufferedReader(new FileReader(testInfoInputFile));

            progressEventQueue = new ArrayBlockingQueue<TestRunProgressEvent>(100);
            final TestFrameworkTestInfoReader testInfoReader = objectFactory.createTestInfoReader(testInfoInputFileReader);
            ioAcceptorProvider = new ExternalIoAcceptorProvider();

            controlServer = new DefaultTestControlServer(ioAcceptorProvider, testInfoReader, progressEventQueue);

            serverPort = controlServer.start();
        }
        catch ( IOException e ) {
            throw new GradleException("failed to startup test control server", e);
        }
    }

    void startupTestClient(TestSuite testSuite) {

        ExecHandleBuilder clientVmHandleBuilder = new ExecHandleBuilder().
                execCommand(JavaEnvUtils.getJdkExecutable("java")).
                arguments("-cp").
                arguments(new File(BootstrapUtil.getGradleHomeLibDir(), "gradle-" + new GradleVersionProperties().getVersion() + ".jar").getAbsolutePath()). // TODO limit this classpath
                arguments(
                    org.gradle.api.testing.execution.control.TestControlClientMain.class.getName(),
                    String.valueOf(serverPort)
        );

        clientVmHandle = clientVmHandleBuilder.getExecHandle();

        // TODO replace whit report generation progress event consumer
        testRunProgressEventConsumer = new DoNothingBlockingQueueItemConsumer<TestRunProgressEvent>(progressEventQueue, 100L, TimeUnit.MILLISECONDS);

    }

    void runTests(TestSuite testSuite) {

    }

    void stopTestClient(TestSuite testSuite) {


        // wait for all progress events to be processed.
        while ( !progressEventQueue.isEmpty() ) {
            Thread.yield();
        }
        testRunProgressEventConsumer.stopConsuming();
    }

    void stopTestServer(TestSuite testSuite) {
        IOUtils.closeQuietly(testInfoInputFileReader);
    }
}
