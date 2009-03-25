package org.gradle.api.testing;

import org.gradle.external.junit.JUnitTestFramework;
import org.gradle.external.testng.TestNGTestFramework;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class keeps track of the available/supported test frameworks.
 *
 * The test framework registry keeps both a list and a map of the registered test frameworks.
 * - For test detection the order of the test framework registration is important.
 * - For general use an id -> test framework map is also kept.
 *
 * @author Tom Eyckmans
 */
class DefaultTestFrameworkRegistry implements TestFrameworkRegistry {

    private final Lock registryLock;
    private final List<TestFramework> orderedRegistry = new ArrayList<TestFramework>();
    private final Map<String, TestFramework> registry = new HashMap<String, TestFramework>(); 

    DefaultTestFrameworkRegistry() {
        registryLock = new ReentrantLock();

        init();
    }

    /**
     * The init method adds JUnit and TestNG to the registry.
     *
     * TODO remove when the actual OSGI whiteboard pattern is used.
     */
    private void init() {
        add(new JUnitTestFramework());
        add(new TestNGTestFramework());
    }

    public void add(TestFramework testFramework) {
        if ( testFramework == null ) throw new IllegalArgumentException("testFramework == null!");
        registryLock.lock();
        try {
            orderedRegistry.add(testFramework);
            registry.put(testFramework.getId(), testFramework);
        }
        finally {
            registryLock.unlock();
        }
    }

    public void remove(TestFramework testFramework) {
        if ( testFramework == null ) throw new IllegalArgumentException("testFramework == null!");
        remove(testFramework.getId());
    }

    public void remove(String testFrameworkId) {
        if ( StringUtils.isEmpty(testFrameworkId) ) throw new IllegalArgumentException("testFrameworkId == empty!");
        registryLock.lock();
        try {
            if ( registry.containsKey(testFrameworkId)) throw new IllegalArgumentException("testFramework not registered!");
            // use the object in the map to remove from the list to prevent a possible object identity problem
            final TestFramework removingTestFramework = registry.remove(testFrameworkId);
            orderedRegistry.remove(removingTestFramework);
        }
        finally {
            registryLock.unlock();
        }
    }

    public TestFramework getTestFramework(String id) {
        if ( StringUtils.isEmpty(id) ) throw new IllegalArgumentException("id == empty!");
        registryLock.lock();
        try {
            return registry.get(id);
        }
        finally {
            registryLock.unlock();
        }
    }

    public List<TestFramework> getTestFrameworks() {
        registryLock.lock();
        try {
            return Collections.unmodifiableList(new ArrayList<TestFramework>(orderedRegistry));
        }
        finally {
            registryLock.unlock();
        }
    }

}
