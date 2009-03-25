package org.gradle.api.testing.execution.control.message;

import org.gradle.api.testing.execution.control.TestControlMessage;
import org.gradle.api.testing.TestInfo;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Tom Eyckmans
 */
public class GetNextActionRunTestResponse implements TestControlMessage {

    private TestInfo testInfo;

    public GetNextActionRunTestResponse(TestInfo testInfo) {
        if ( testInfo == null ) throw new IllegalArgumentException("testInfo is null!");
        this.testInfo = testInfo;
    }

    public TestInfo getTestInfo() {
        return testInfo;
    }

    private void writeObject(ObjectOutputStream out)
         throws IOException {
        out.writeObject(testInfo);
    }

    private void readObject(ObjectInputStream in)
         throws IOException, ClassNotFoundException {
        testInfo = (TestInfo)in.readObject();
    }

}
