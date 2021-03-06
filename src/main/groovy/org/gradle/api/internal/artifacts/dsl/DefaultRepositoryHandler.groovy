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
package org.gradle.api.internal.artifacts.dsl

import org.apache.ivy.plugins.resolver.DependencyResolver
import org.apache.ivy.plugins.resolver.FileSystemResolver
import org.gradle.api.InvalidUserDataException
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.internal.artifacts.DefaultResolverContainer
import org.gradle.api.internal.artifacts.ivyservice.ResolverFactory
import org.gradle.api.plugins.Convention
import org.gradle.util.HashUtil
import org.gradle.util.WrapUtil
import org.gradle.api.artifacts.maven.GroovyMavenDeployer
import org.gradle.api.artifacts.maven.MavenResolver

/**
 * @author Hans Dockter
 */
class DefaultRepositoryHandler extends DefaultResolverContainer implements RepositoryHandler {
    DefaultRepositoryHandler(ResolverFactory resolverFactory, Convention convention) {
        super(resolverFactory, convention)
    }

    FileSystemResolver flatDir(Map args) {
        Object[] rootDirs = getFlatDirRootDirs(args);
        FileSystemResolver resolver = resolverFactory.createFlatDirResolver(
                getNameFromMap(args, HashUtil.createHash(rootDirs.join(""))),
                (rootDirs.collect { it as File }) as File[]);
        return (FileSystemResolver) add(resolver);
    }

    private String getNameFromMap(Map args, String defaultName) {
        return args.name ?: defaultName
    }

    private Object[] getFlatDirRootDirs(Map args) {
        List dirs = createStringifiedListFromMapArg(args, "dirs");
        if (dirs == null) {
            throw new InvalidUserDataException("You must specify dirs for the flat dir repository.");
        }
        return dirs.toArray();
    }

    private List<String> createStringifiedListFromMapArg(Map args, String argName) {
        Object dirs = args[argName];
        if (dirs == null) { return null }
        Iterable<Object> iterable;
        if (dirs instanceof Iterable) {
            iterable = (Iterable<Object>) dirs;
        } else {
            iterable = WrapUtil.toSet(dirs);
        }
        List list = new ArrayList();
        for (Object o : iterable) {
            list.add(o.toString());
        }
        return list;
    }

    public DependencyResolver mavenCentral() {
        return mavenCentral(Collections.emptyMap());
    }

    public DependencyResolver mavenCentral(Map args) {
        List<String> urls = createStringifiedListFromMapArg(args, "urls");
        return add(resolverFactory.createMavenRepoResolver(
                getNameFromMap(args, DEFAULT_MAVEN_CENTRAL_REPO_NAME),
                MAVEN_CENTRAL_URL,
                urls == null ? [] as String[] : urls.toArray(new String[urls.size()])));
    }

    public DependencyResolver mavenRepo(Map args) {
        List urls = createStringifiedListFromMapArg(args, "urls");
        if (urls == null) {
            throw new InvalidUserDataException("You must specify a urls for a Maven repo.");
        }
        return add(resolverFactory.createMavenRepoResolver(
                getNameFromMap(args, urls[0] as String),
                urls[0] as String,
                urls.size() == 1 ? [] as String[] : urls[1..-1] as String[]))
    }

    public GroovyMavenDeployer mavenDeployer(Map args = [:]) {
        GroovyMavenDeployer mavenDeployer = createMavenDeployer(args)
        return (GroovyMavenDeployer) add(mavenDeployer);
    }

    private GroovyMavenDeployer createMavenDeployer(Map args) {
        GroovyMavenDeployer mavenDeployer = createMavenDeployer("dummyName")
        String defaultName = RepositoryHandler.DEFAULT_MAVEN_DEPLOYER_NAME + "-" + System.identityHashCode(mavenDeployer);
        mavenDeployer.setName(getNameFromMap(args, defaultName))
        return mavenDeployer
    }

    public GroovyMavenDeployer mavenDeployer(Map args = [:], Closure configureClosure) {
        GroovyMavenDeployer mavenDeployer = createMavenDeployer(args)
        return (GroovyMavenDeployer) add(mavenDeployer, configureClosure);
    }

    public MavenResolver mavenInstaller(Map args = [:]) {
        MavenResolver mavenInstaller = createMavenInstaller(args)
        return (MavenResolver) add(mavenInstaller);
    }

    public MavenResolver mavenInstaller(Map args = [:], Closure configureClosure) {
        MavenResolver mavenInstaller = createMavenInstaller(args)
        return (MavenResolver) add(mavenInstaller, configureClosure);
    }

    private MavenResolver createMavenInstaller(Map args) {
        MavenResolver mavenInstaller = createMavenInstaller("dummyName")
        String defaultName = RepositoryHandler.DEFAULT_MAVEN_INSTALLER_NAME + "-" + System.identityHashCode(mavenInstaller);
        mavenInstaller.setName(getNameFromMap(args, defaultName))
        return mavenInstaller
    }
}
