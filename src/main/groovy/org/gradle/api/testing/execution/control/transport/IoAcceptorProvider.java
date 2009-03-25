package org.gradle.api.testing.execution.control.transport;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.service.IoAcceptor;

import java.io.IOException;

/**
 * @author Tom Eyckmans
 */
public interface IoAcceptorProvider {

    IoAcceptor getIoAcceptor(IoHandlerAdapter handler) throws IOException;

    int getLocalPort(IoAcceptor ioAcceptor);
}
