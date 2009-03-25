package org.gradle.external.testng;

import org.gradle.api.testing.AbstractTestInfo;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @author Tom Eyckmans
 */
public class TestNGTestClassTestInfo extends AbstractTestInfo {

    public TestNGTestClassTestInfo(String testClassName) {
        super(testClassName);
    }

    private void writeObject(ObjectOutputStream out)
         throws IOException {
        out.writeUTF(testClassName);
    }

    private void readObject(ObjectInputStream in)
         throws IOException, ClassNotFoundException {
        testClassName = in.readUTF();
    }
}