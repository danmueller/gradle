/*
 * Copyright 2009 the original author or authors.
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
package org.gradle.api.internal.artifacts.configurations;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.specs.Spec;
import org.apache.ivy.core.module.descriptor.Artifact;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Hans Dockter
 */
public class Configurations {
    public static Set<String> getNames(Collection<Configuration> configurations, boolean includeExtended) {
        Set<Configuration> allConfigurations = new HashSet<Configuration>(configurations);
        if (includeExtended) {
            allConfigurations = createAllConfigurations(configurations);
        }
        Set<String> names = new HashSet<String>();
        for (Configuration configuration : allConfigurations) {
            names.add(configuration.getName());
        }
        return names;
    }

    public static Set<String> getNames(Collection<Configuration> configurations) {
        return getNames(configurations, false);
    }

    private static Set<Configuration> createAllConfigurations(Collection<Configuration> configurations) {
        Set<Configuration> allConfigurations = new HashSet<Configuration>();
        for (Configuration configuration : configurations) {
            allConfigurations.addAll(configuration.getHierarchy());
        }
        return allConfigurations;
    }

    public static String uploadInternalTaskName(String configurationName) {
        return String.format("upload%sInternal", getCapitalName(configurationName));
    }

    public static String uploadTaskName(String configurationName) {
        return String.format("upload%s", getCapitalName(configurationName));
    }


    private static String getCapitalName(String configurationName) {
        return configurationName.substring(0, 1).toUpperCase() + configurationName.substring(1);
    }

    public static Set<Dependency> getDependencies(List<Configuration> configurations, Spec<Dependency> dependencySpec) {
        Set<Dependency> dependencies = new HashSet<Dependency>();
        for (Configuration configuration : configurations) {
            for (Dependency dependency : configuration.getDependencies()) {
                if (dependencySpec.isSatisfiedBy(dependency)) {
                    dependencies.add(dependency);
                }
            }
        }
        return dependencies;
    }

    public static Set<PublishArtifact> getArtifacts(List<Configuration> configurations, Spec<PublishArtifact> artifactSpec) {
        Set<PublishArtifact> artifacts = new HashSet<PublishArtifact>();
        for (Configuration configuration : configurations) {
            for (PublishArtifact artifact : configuration.getArtifacts()) {
                if (artifactSpec.isSatisfiedBy(artifact)) {
                    artifacts.add(artifact);
                }
            }
        }
        return artifacts;
    }
}
