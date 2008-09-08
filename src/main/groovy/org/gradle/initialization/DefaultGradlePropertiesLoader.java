/*
 * Copyright 2007-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.initialization;

import org.gradle.StartParameter;
import org.gradle.api.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * @author Hans Dockter
 */
public class DefaultGradlePropertiesLoader implements IGradlePropertiesLoader {
    private static Logger logger = LoggerFactory.getLogger(DefaultGradlePropertiesLoader.class);

    private Map<String, String> gradleProperties = new HashMap<String, String>();

    public void loadProperties(File settingsDir, StartParameter startParameter, Map<String, String> systemProperties,
                               Map<String, String> envProperties) {
        addGradleProperties(
                new File(settingsDir, Project.GRADLE_PROPERTIES),
                new File(startParameter.getGradleUserHomeDir(), Project.GRADLE_PROPERTIES));
        setSystemProperties(startParameter.getSystemPropertiesArgs());
        gradleProperties.putAll(getEnvProjectProperties(envProperties));
        gradleProperties.putAll(getSystemProjectProperties(systemProperties));
        gradleProperties.putAll(startParameter.getProjectProperties());
    }

    private void addGradleProperties(File... files) {
        for (File propertyFile : files) {
            if (propertyFile.isFile()) {
                Properties properties = new Properties();
                try {
                    properties.load(new FileInputStream(propertyFile));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                gradleProperties.putAll(new HashMap(properties));
            }
        }
    }

    public Map<String, String> getGradleProperties() {
        return gradleProperties;
    }

    public void setGradleProperties(Map<String, String> gradleProperties) {
        this.gradleProperties = gradleProperties;
    }

    private Map getSystemProjectProperties(Map<String, String> systemProperties) {
        Map<String, String> systemProjectProperties = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : systemProperties.entrySet()) {
            if (entry.getKey().startsWith(SYSTEM_PROJECT_PROPERTIES_PREFIX) &&
                    entry.getKey().length() > SYSTEM_PROJECT_PROPERTIES_PREFIX.length()) {
                systemProjectProperties.put(entry.getKey().substring(SYSTEM_PROJECT_PROPERTIES_PREFIX.length()), entry.getValue());
            }
        }
        logger.debug("Found system project properties: {}", systemProjectProperties.keySet());
        return systemProjectProperties;
    }

    private Map getEnvProjectProperties(Map<String, String> envProperties) {
        Map<String, String> envProjectProperties = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : envProperties.entrySet()) {
            if (entry.getKey().startsWith(ENV_PROJECT_PROPERTIES_PREFIX) &&
                    entry.getKey().length() > ENV_PROJECT_PROPERTIES_PREFIX.length()) {
                envProjectProperties.put(entry.getKey().substring(ENV_PROJECT_PROPERTIES_PREFIX.length()), entry.getValue());
            }
        }
        logger.debug("Found env project properties: {}", envProjectProperties.keySet());
        return envProjectProperties;
    }

    private void setSystemProperties(Map properties) {
        System.getProperties().putAll(properties);
        addSystemPropertiesFromGradleProperties();
    }

    private void addSystemPropertiesFromGradleProperties() {
        for (String key : gradleProperties.keySet()) {
            if (key.startsWith(Project.SYSTEM_PROP_PREFIX + '.')) {
                System.setProperty(key.substring((Project.SYSTEM_PROP_PREFIX + '.').length()), gradleProperties.get(key));
            }
        }
    }
}