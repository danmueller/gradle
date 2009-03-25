package org.gradle.api.testing.execution.control.message;

import org.gradle.api.testing.execution.control.TestControlMessage;

import java.io.IOException;

/**
 * @author Tom Eyckmans
 */
public class GetNextActionRequest implements TestControlMessage {

   private void writeObject(java.io.ObjectOutputStream out)
         throws IOException {
    }

    private void readObject(java.io.ObjectInputStream in)
         throws IOException, ClassNotFoundException {
    }

}
