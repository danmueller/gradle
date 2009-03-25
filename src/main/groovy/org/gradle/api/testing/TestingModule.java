package org.gradle.api.testing;

import com.google.inject.AbstractModule;

import java.util.Properties;
import java.io.File;

/**
 * @author Tom Eyckmans
 */
public class TestingModule extends AbstractModule {

    public void configure() {
        final TestFrameworkRegistry testFrameworkRegistry = new DefaultTestFrameworkRegistry();

        final Properties testingProperties = new Properties();
        final File testingPropertiesFile = new File("");

        bind(TestFrameworkRegistry.class).toInstance(testFrameworkRegistry);
        bind(TestingObjectFactory.class).toInstance(new DefaultTestingObjectFactory());
    }
}
