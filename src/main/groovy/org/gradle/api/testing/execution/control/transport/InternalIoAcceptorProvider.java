package org.gradle.api.testing.execution.control.transport;

import org.apache.mina.transport.vmpipe.VmPipeAddress;
import org.apache.mina.transport.vmpipe.VmPipeAcceptor;
import org.apache.mina.core.service.IoAcceptor;

import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * @author Tom Eyckmans
 */
public class InternalIoAcceptorProvider extends AbstractIoAcceptorProvider<VmPipeAcceptor> {

    public InternalIoAcceptorProvider() {
        super();
    }

    public InternalIoAcceptorProvider(int port) {
        super(port);
    }

    protected VmPipeAcceptor instanciateIoAcceptor() {
        return new VmPipeAcceptor(Executors.newCachedThreadPool());
    }

    protected void bind(VmPipeAcceptor acceptor) throws IOException {
        acceptor.bind(new VmPipeAddress(port));
    }

    public int getLocalPort(IoAcceptor ioAcceptor) {
        return ((VmPipeAcceptor)ioAcceptor).getLocalAddress().getPort();
    }
}
