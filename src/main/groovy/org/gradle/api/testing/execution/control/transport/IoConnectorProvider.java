package org.gradle.api.testing.execution.control.transport;

import org.apache.mina.core.service.IoConnector;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * @author Tom Eyckmans
 */
public interface IoConnectorProvider {

    IoConnector getIoConnector() throws IOException;

    SocketAddress getSocketAddress();
}
