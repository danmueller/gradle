package org.gradle.api.testing.execution.control;

import java.net.SocketAddress;

/**
 * @author Tom Eyckmans
 */
public interface TestControlServer {

    int start();

    void stop();

}
