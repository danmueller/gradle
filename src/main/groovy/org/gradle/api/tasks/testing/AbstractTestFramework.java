package org.gradle.api.tasks.testing;

import org.gradle.api.Project;
import org.gradle.api.testing.TestFramework;
import org.gradle.api.testing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.objectweb.asm.ClassReader;

import java.util.*;
import java.io.*;

/**
 * @author Tom Eyckmans
 */
public abstract class AbstractTestFramework implements TestFramework {

    private static final Logger logger = LoggerFactory.getLogger(AbstractTestFramework.class);
    public static final String USE_OF_CORRECT_TEST_FRAMEWORK =
        "Make sure the correct TestFramework is in use. \n" +
        "            - Call useJUnit(), useTestNG() or useTestFramework(<your own TestFramework implementation class>) as first statement in the test { } block. \n" +
        "            - Set the test.framework.default property in a gradle.properties file ";

    protected final String id;
    protected final String name;
    protected final TestFrameworkObjectFactory objectFactory;

    protected AbstractTestFramework(String id, String name, TestFrameworkObjectFactory objectFactory) {
        if ( StringUtils.isEmpty(id) ) throw new IllegalArgumentException("id == empty!");
        if ( StringUtils.isEmpty(name) ) throw new IllegalArgumentException("name == empty!");
        if ( objectFactory == null ) throw new IllegalArgumentException("objectFactory == null!");
        this.id = id;
        this.name = name;
        this.objectFactory = objectFactory;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TestFrameworkObjectFactory getObjectFactory() {
        return objectFactory;
    }

}
