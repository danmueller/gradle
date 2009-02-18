/*
 * Copyright 2007 the original author or authors.
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

package org.gradle.api.tasks.compile;

import org.gradle.api.*;
import org.gradle.api.plugins.ConventionValue;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.artifacts.ConfigurationResolveInstructionModifier;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.util.ExistingDirsFilter;
import org.gradle.util.GUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.math.BigDecimal;

/**
* @author Hans Dockter
*/
public class Compile extends ConventionTask {

    /**
     * The directories with the sources to compile
     */
    private final ConventionValue<List<File>> srcDirs;

    /**
     * The directory where to put the compiled classes (.class files)
     */
    private final ConventionValue<File> destinationDir;

    /**
     * The sourceCompatibility used by the Java compiler for your code. (e.g. 1.5)
     */
    protected final ConventionValue<BigDecimal> sourceCompatibility;

    /**
     * The targetCompatibility used by the Java compiler for your code. (e.g. 1.5)
     */
    protected final ConventionValue<BigDecimal> targetCompatibility;

    private ConfigurationResolveInstructionModifier resolveInstructionModifier;

    /**
     * This property is used internally by Gradle. It is usually not used by build scripts.
     * A list of files added to the compile classpath. The files should point to jars or directories containing
     * class files. The files added here are not shared in a multi-project build and are not mentioned in
     * a dependency descriptor if you upload your library to a repository.
     */
    private List unmanagedClasspath = null;

    /**
     * Options for the compiler. The compile is delegated to the ant javac task. This property contains almost
     * all of the properties available for the ant javac task.
     */
    private CompileOptions options = new CompileOptions();

    /**
     * Include pattern for which files should be compiled (e.g. '**&#2F;org/gradle/package1/')).
     */
    private List includes = new ArrayList();

    /**
     * Exclude pattern for which files should be compiled (e.g. '**&#2F;org/gradle/package2/A*.java').
     */
    private List excludes = new ArrayList();

    private DependencyManager dependencyManager;

    protected ExistingDirsFilter existentDirsFilter = new ExistingDirsFilter();

    protected AntJavac antCompile = new AntJavac();

    protected ClasspathConverter classpathConverter = new ClasspathConverter();

    public Compile(Project project, String name) {
        super(project, name);
        doFirst(new TaskAction() {
            public void execute(Task task) {
                compile(task);
            }
        });

        srcDirs = getConventionValue(JavaPluginConvention.ValueNames.srcDirs);
        destinationDir = getConventionValue(JavaPluginConvention.ValueNames.classesDir);
        sourceCompatibility = getConventionValue(JavaPluginConvention.ValueNames.sourceCompatibility);
        targetCompatibility = getConventionValue(JavaPluginConvention.ValueNames.targetCompatibility);
    }

    protected void compile(Task task) {
        if (antCompile == null) {
            throw new InvalidUserDataException("The ant compile command must be set!");
        }
        getDestinationDir().mkdirs();
        List existingSourceDirs = existentDirsFilter.checkDestDirAndFindExistingDirsAndThrowStopActionIfNone(
                getDestinationDir(), getSrcDirs());

        if (!GUtil.isTrue(getSourceCompatibility()) || !GUtil.isTrue(getTargetCompatibility())) {
            throw new InvalidUserDataException("The sourceCompatibility and targetCompatibility must be set!");
        }

        antCompile.execute(existingSourceDirs, includes, excludes, getDestinationDir(), getClasspath(), getSourceCompatibility().toString(),
                getTargetCompatibility().toString(), options, getProject().getAnt());
    }

    public List getClasspath() {
        List classpath = GUtil.addLists(classpathConverter.createFileClasspath(getProject().getRootDir(), getUnmanagedClasspath()),
                getDependencyManager().configuration(resolveInstructionModifier.getConfiguration()).resolve(resolveInstructionModifier));
        return classpath;
    }

    /**
     * Add the elements to the unmanaged classpath.
     */
    public Compile unmanagedClasspath(Object... elements) {
        if (unmanagedClasspath == null) {
            List conventionPath = getUnmanagedClasspath();
            if (conventionPath != null) {
                unmanagedClasspath = conventionPath;
            } else {
                unmanagedClasspath = new ArrayList();
            }
        }
        GUtil.flatten(Arrays.asList(elements), unmanagedClasspath);
        return this;
    }

    public Compile include(String[] includes) {
        GUtil.flatten(Arrays.asList(includes), this.includes);
        return this;
    }

    public Compile exclude(String[] excludes) {
        GUtil.flatten(Arrays.asList(excludes), this.excludes);
        return this;
    }

    public List<File> getSrcDirs() {
        return srcDirs.getValue();
    }

    public void setSrcDirs(List<File> srcDirs) {
        this.srcDirs.setValue(srcDirs);
    }

    public File getDestinationDir() {
        return destinationDir.getValue();
    }

    public void setDestinationDir(File destinationDir) {
        this.destinationDir.setValue(destinationDir);
    }

    public BigDecimal getSourceCompatibility() {
        return sourceCompatibility.getValue();
    }

    public void setSourceCompatibility(BigDecimal sourceCompatibility) {
        this.sourceCompatibility.setValue(sourceCompatibility);
    }

    public BigDecimal getTargetCompatibility() {
        return targetCompatibility.getValue();
    }

    public void setTargetCompatibility(BigDecimal targetCompatibility) {
        this.targetCompatibility.setValue(targetCompatibility);
    }

    public List getUnmanagedClasspath() {
        return (List) conv(unmanagedClasspath, "unmanagedClasspath");
    }

    public void setUnmanagedClasspath(List unmanagedClasspath) {
        this.unmanagedClasspath = unmanagedClasspath;
    }

    public CompileOptions getOptions() {
        return options;
    }

    public void setOptions(CompileOptions options) {
        this.options = options;
    }

    public List getIncludes() {
        return includes;
    }

    public void setIncludes(List includes) {
        this.includes = includes;
    }

    public List getExcludes() {
        return excludes;
    }

    public void setExcludes(List excludes) {
        this.excludes = excludes;
    }

    public DependencyManager getDependencyManager() {
        return (DependencyManager) conv(dependencyManager, "dependencyManager");
    }

    public void setDependencyManager(DependencyManager dependencyManager) {
        this.dependencyManager = dependencyManager;
    }

    public ConfigurationResolveInstructionModifier getResolveInstruction() {
        return resolveInstructionModifier;
    }

    public void setResolveInstruction(ConfigurationResolveInstructionModifier resolveInstructionModifier) {
        this.resolveInstructionModifier = resolveInstructionModifier;
    }
}
