package org.gradle.api.testing.execution.control;

import org.gradle.api.testing.execution.control.transport.IoAcceptorProvider;
import org.gradle.api.testing.execution.control.transport.ExternalIoAcceptorProvider;
import org.gradle.api.testing.execution.progress.event.BeginTestRunProgressEvent;
import org.gradle.api.testing.execution.progress.event.BeginTestRunProgressEventProcessor;
import org.gradle.api.testing.execution.progress.event.EndTestRunProgressEvent;
import org.gradle.api.testing.execution.progress.event.EndTestRunProgressEventProcessor;
import org.gradle.api.testing.execution.progress.TestRunProgressEvent;
import org.gradle.api.testing.execution.progress.TestRunProgressEventDispatcher;
import org.gradle.api.testing.execution.progress.DefaultTestRunProgressEventDispatcher;
import org.gradle.api.testing.execution.progress.TestRunProgressEventQueueConsumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.net.SocketAddress;

/**
 * @author Tom Eyckmans
 */
public class TestControlServerMain {
    public static void main(String[] args) {

        final BlockingQueue<TestRunProgressEvent> progressEventQueue = new ArrayBlockingQueue<TestRunProgressEvent>(100);
        final IoAcceptorProvider ioAcceptorProvider = new ExternalIoAcceptorProvider();
        final TestControlServer server = new DefaultTestControlServer(ioAcceptorProvider, null, progressEventQueue);

        final TestRunProgressEventDispatcher eventDispatcher = new DefaultTestRunProgressEventDispatcher();
        final TestRunProgressEventQueueConsumer eventQueueConsumer = new TestRunProgressEventQueueConsumer(progressEventQueue, eventDispatcher);

            eventDispatcher.registerEventProcessor(BeginTestRunProgressEvent.class, new BeginTestRunProgressEventProcessor());
            eventDispatcher.registerEventProcessor(EndTestRunProgressEvent.class, new EndTestRunProgressEventProcessor(eventQueueConsumer));


        new Thread(eventQueueConsumer).start();
        int port = server.start();
        System.out.println("main ended (port: " + port + ")");
    }
}
