package org.gradle.api.testing.execution.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
import org.gradle.api.testing.execution.control.transport.IoAcceptorProvider;
import org.gradle.api.testing.execution.control.message.GetNextActionRequest;
import org.gradle.api.testing.execution.control.message.GetNextActionRunTestResponse;
import org.gradle.api.testing.execution.control.message.GetNextActionExitResponse;
import org.gradle.api.testing.execution.progress.TestRunProgressEvent;
import org.gradle.api.testing.TestFrameworkTestInfoReader;
import org.gradle.api.testing.TestInfo;
import org.gradle.api.GradleException;
import org.gradle.util.BlockingQueueItemProducer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.io.IOException;

/**
 * @author Tom Eyckmans
 */
public class DefaultTestControlServer extends IoHandlerAdapter implements TestControlServer {
    private static final Logger logger = LoggerFactory.getLogger(DefaultTestControlServer.class);
    private final IoAcceptorProvider ioAcceptorProvider;
    private final TestFrameworkTestInfoReader testInfoReader;
    private final BlockingQueueItemProducer<TestRunProgressEvent> testRunProgressEventProducer;

    private IoAcceptor ioAcceptor;

    public DefaultTestControlServer(IoAcceptorProvider ioAcceptorProvider, TestFrameworkTestInfoReader testInfoReader, BlockingQueue<TestRunProgressEvent> testRunProgressEvents) {
        if ( ioAcceptorProvider == null ) throw new IllegalArgumentException("socketAcceptorProvider is null!");
        if ( testInfoReader == null ) throw new IllegalArgumentException("testInfoReader is null!");
        if ( testRunProgressEvents == null ) throw new IllegalArgumentException("testRunProgressEvents is null!");
        this.ioAcceptorProvider = ioAcceptorProvider;
        this.testInfoReader = testInfoReader;
        this.testRunProgressEventProducer = new BlockingQueueItemProducer<TestRunProgressEvent>(testRunProgressEvents, 1L, TimeUnit.SECONDS);
    }

    public int start() {
        int port = 0;
        try {
            ioAcceptor = ioAcceptorProvider.getIoAcceptor(this);
            port = ioAcceptorProvider.getLocalPort(ioAcceptor);
        }
        catch ( IOException e ) {
            throw new GradleException("failed to start test run progress receive server", e);
        }
        return port;
    }

    public void stop() {
        ioAcceptor.unbind();
    }

    @Override
    public void messageReceived(IoSession ioSession, Object message) throws Exception {
        if ( message.getClass() == GetNextActionRequest.class ) {
            final TestInfo testInfo = testInfoReader.read();
            if ( testInfo != null ) {
                ioSession.write(new GetNextActionRunTestResponse(testInfo));
            }
            else {
                ioSession.write(new GetNextActionExitResponse());
            }
        }
        else {
            testRunProgressEventProducer.produce((TestRunProgressEvent) message);
        }
    }
}
