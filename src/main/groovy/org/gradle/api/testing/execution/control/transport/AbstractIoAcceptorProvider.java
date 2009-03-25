package org.gradle.api.testing.execution.control.transport;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Tom Eyckmans
 */
public abstract class AbstractIoAcceptorProvider<T extends IoAcceptor> implements IoAcceptorProvider {
    private static final Logger logger = LoggerFactory.getLogger(AbstractIoAcceptorProvider.class);
    protected final int port;

    protected AbstractIoAcceptorProvider() {
        this.port = 0;
    }

    protected AbstractIoAcceptorProvider(int port) {
        this.port = port;
    }

    protected abstract T instanciateIoAcceptor();
    protected abstract void bind(T acceptor) throws IOException;

    public IoAcceptor getIoAcceptor(IoHandlerAdapter handler) throws IOException {
        final T acceptor = instanciateIoAcceptor();

        if ( logger.isDebugEnabled())
            acceptor.getFilterChain().addLast("logger", new LoggingFilter());
        acceptor.getFilterChain().addLast(
                              "codec",
                              new ProtocolCodecFilter(
                                      new ObjectSerializationCodecFactory()));

        acceptor.setHandler(handler);

        bind(acceptor);

        return acceptor;
    }
}
