package org.gradle.api.testing.execution.control.transport;

import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Tom Eyckmans
 */
public abstract class AbstractIoConnectorProvider<T extends IoConnector> implements IoConnectorProvider {
    private static final Logger logger = LoggerFactory.getLogger(AbstractIoConnectorProvider.class);
    protected final int port;

    protected AbstractIoConnectorProvider() {
        port = 0;
    }

    protected AbstractIoConnectorProvider(int port) {
        this.port = port;
    }

    protected abstract T instanciateConnector();

    public final IoConnector getIoConnector() throws IOException {
        T connector = instanciateConnector();

        if ( logger.isDebugEnabled())
            connector.getFilterChain().addLast("logger", new LoggingFilter());
        connector.getFilterChain().addLast(
                              "codec",
                              new ProtocolCodecFilter(
                                      new ObjectSerializationCodecFactory()));

        connector.setHandler(new IoHandlerAdapter());

        return connector;
    }
}
