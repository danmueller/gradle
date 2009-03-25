package org.gradle.api.testing;

import java.util.List;

/**
 * @author Tom Eyckmans
 */
public interface TestFrameworkRegistry {
    void add(TestFramework testFramework);

    void remove(TestFramework testFramework);

    void remove(String testFrameworkId);

    TestFramework getTestFramework(String id);

    List<TestFramework> getTestFrameworks();
}
