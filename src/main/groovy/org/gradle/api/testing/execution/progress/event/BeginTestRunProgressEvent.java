package org.gradle.api.testing.execution.progress.event;

import org.gradle.api.testing.execution.progress.TestRunProgressEvent;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/**
 * @author Tom Eyckmans
 */
public class BeginTestRunProgressEvent implements TestRunProgressEvent {

    private long createMillis;

    public BeginTestRunProgressEvent() {
        this.createMillis = System.currentTimeMillis();
    }

    public long getCreateMillis() {
        return createMillis;
    }

    private void writeObject(ObjectOutputStream out)
         throws IOException {
        out.writeLong(createMillis);
    }

    private void readObject(ObjectInputStream in)
         throws IOException, ClassNotFoundException {
        createMillis = in.readLong(); 
    }

}
