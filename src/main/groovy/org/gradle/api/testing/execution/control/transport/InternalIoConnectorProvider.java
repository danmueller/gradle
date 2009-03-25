package org.gradle.api.testing.execution.control.transport;

import org.apache.mina.transport.vmpipe.VmPipeConnector;
import org.apache.mina.transport.vmpipe.VmPipeAddress;

import java.net.SocketAddress;

/**
 * @author Tom Eyckmans
 */
public class InternalIoConnectorProvider extends AbstractIoConnectorProvider<VmPipeConnector> {
    protected VmPipeConnector instanciateConnector() {
        return new VmPipeConnector();
    }

    public SocketAddress getSocketAddress() {
        return new VmPipeAddress(port);
    }
}
