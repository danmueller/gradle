package org.gradle.api.testing;

import java.io.BufferedWriter;
import java.io.BufferedReader;

/**
 * @author Tom Eyckmans
 */
public interface TestFrameworkObjectFactory {

    TestFrameworkDetector createDetector();

    TestFrameworkTestInfoWriter createTestInfoWriter(BufferedWriter outputWriter);

    TestFrameworkTestInfoReader createTestInfoReader(BufferedReader inputReader);

    TestFrameworkRunner createRunner();

}
