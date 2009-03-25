package org.gradle.api.testing.execution.control.transport;

import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.core.service.IoAcceptor;

import java.net.InetSocketAddress;
import java.io.IOException;

/**
 * @author Tom Eyckmans
 */
public class ExternalIoAcceptorProvider extends AbstractIoAcceptorProvider<NioSocketAcceptor> {

    public ExternalIoAcceptorProvider() {
        super();
    }

    public ExternalIoAcceptorProvider(int port) {
        super(port);
    }

    protected NioSocketAcceptor instanciateIoAcceptor() {
        NioSocketAcceptor acceptor = new NioSocketAcceptor();

        acceptor.setReuseAddress(true);

        return acceptor;
    }

    protected void bind(NioSocketAcceptor acceptor) throws IOException {
        acceptor.bind(new InetSocketAddress(port));
    }

    public int getLocalPort(IoAcceptor ioAcceptor) {
        return ((NioSocketAcceptor)ioAcceptor).getLocalAddress().getPort();
    }
}
