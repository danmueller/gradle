package org.gradle.api.testing.execution.control.transport;

import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author Tom Eyckmans
 */
public class ExternalIoConnectorProvider extends AbstractIoConnectorProvider<NioSocketConnector> {

    public ExternalIoConnectorProvider() {
    }

    public ExternalIoConnectorProvider(int port) {
        super(port);
    }

    protected NioSocketConnector instanciateConnector() {
        return new NioSocketConnector();
    }

    public SocketAddress getSocketAddress() {
        return new InetSocketAddress(port);
    }
}
