package org.gradle.util;

import java.util.Properties;

/**
 * @author Tom Eyckmans
 */
public class GradleVersionProperties {
    public static final String BUILD_TIME = "buildTime";
    public static final String VERSION = "version";
    public static final String FILE_NAME = "version.properties";

    private final Properties versionProperties;

    public GradleVersionProperties() {
        versionProperties = GUtil.loadProperties(getClass().getResourceAsStream('/' + FILE_NAME));
    }

    public String getBuildTime() {
        return versionProperties.getProperty(BUILD_TIME);
    }

    public String getVersion() {
        return versionProperties.getProperty(VERSION);
    }
}
