package org.gradle.api.testing.execution.control;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.gradle.api.testing.execution.control.transport.IoConnectorProvider;
import org.gradle.api.testing.execution.progress.event.BeginTestRunProgressEvent;
import org.gradle.api.testing.execution.progress.event.EndTestRunProgressEvent;
import org.gradle.api.testing.execution.control.message.GetNextActionRequest;
import org.gradle.api.testing.execution.control.message.GetNextActionRunTestResponse;
import org.gradle.api.GradleException;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;

import java.io.IOException;

/**
 * @author Tom Eyckmans
 */
public class DefaultTestControlClient implements TestControlClient {
    private static final Logger logger = LoggerFactory.getLogger(DefaultTestControlClient.class);
    private final IoConnectorProvider ioConnectorProvider;

    private IoConnector ioConnector;
    private IoSession ioSession;

    public DefaultTestControlClient(IoConnectorProvider ioConnectorProvider) {
        this.ioConnectorProvider = ioConnectorProvider;
    }

    private void open() {
        try {
            ioConnector = ioConnectorProvider.getIoConnector();

            final ConnectFuture connectFuture = ioConnector.connect(ioConnectorProvider.getSocketAddress());

            connectFuture.awaitUninterruptibly();

            ioSession = connectFuture.getSession();
        }
        catch ( IOException e ) {
            throw new GradleException("failed to open test run progress client", e);
        }
    }

    private void close() {
        final CloseFuture closeFuture = ioSession.close(false); // false = all requests are flushed.
        closeFuture.awaitUninterruptibly();

        ioConnector.dispose();
    }

    public void reportBegin() {
        open();
        ioSession.write(new BeginTestRunProgressEvent());
    }

    public void reportEnd() {
        ioSession.write(new EndTestRunProgressEvent());
        close();
    }

    public String getNextAction() {
        final WriteFuture writeFuture = ioSession.write(new GetNextActionRequest());
        writeFuture.awaitUninterruptibly();
        final ReadFuture readFuture = ioSession.read();
        readFuture.awaitUninterruptibly();
        final GetNextActionRunTestResponse getNextActionRunTestResponse = (GetNextActionRunTestResponse) readFuture.getMessage();
        
        return getNextActionRunTestResponse.getTestClassName();
    }
}
