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

package org.gradle.initialization

import groovy.mock.interceptor.MockFor
import org.gradle.StartParameter
import org.gradle.api.DependencyManager
import org.gradle.api.DependencyManagerFactory
import org.gradle.api.Project
import org.gradle.api.internal.dependencies.DefaultDependencyManagerFactory
import org.gradle.api.internal.project.ImportsReader
import org.gradle.groovy.scripts.EmptyScript
import org.gradle.groovy.scripts.IScriptProcessor
import org.gradle.groovy.scripts.ISettingsScriptMetaData
import org.gradle.initialization.DefaultSettings
import org.gradle.initialization.RootFinder
import org.gradle.initialization.SettingsProcessor
import org.gradle.util.GFileUtils
import org.gradle.util.HelperUtil
import org.gradle.util.JUnit4GroovyMockery
import org.junit.After
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.gradle.groovy.scripts.FileScriptSource
import org.gradle.groovy.scripts.ScriptSource
import org.gradle.util.ReflectionEqualsMatcher
import org.hamcrest.Matchers

/**
 * @author Hans Dockter
 */
class SettingsProcessorTest {
    static final File TEST_ROOT_DIR = new File('rootDir')
    SettingsProcessor settingsProcessor
    RootFinder expectedRootFinder
    ImportsReader importsReader
    DependencyManagerFactory dependencyManagerFactory
    SettingsFactory settingsFactory
    BuildSourceBuilder buildSourceBuilder
    StartParameter expectedStartParameter
    File buildResolverDir
    File settingsFileDir

    IScriptProcessor scriptProcessorMock
    ISettingsScriptMetaData settingsScriptMetaData

    DefaultSettings expectedSettings
    MockFor settingsFactoryMocker

    JUnit4GroovyMockery context = new JUnit4GroovyMockery()

    @Before public void setUp()  {
        scriptProcessorMock = context.mock(IScriptProcessor)
        settingsScriptMetaData = context.mock(ISettingsScriptMetaData)
        buildResolverDir = HelperUtil.makeNewTestDir('buildResolver')
        settingsFileDir = HelperUtil.makeNewTestDir('settingsDir')
        expectedSettings = new DefaultSettings()
        expectedStartParameter = new StartParameter()
        expectedRootFinder = new RootFinder()
        importsReader = new ImportsReader()
        settingsFactory = new SettingsFactory()
        dependencyManagerFactory = new DefaultDependencyManagerFactory(new File('root'))
        buildSourceBuilder = new BuildSourceBuilder()
        settingsProcessor = new SettingsProcessor(settingsScriptMetaData, scriptProcessorMock, importsReader, settingsFactory, dependencyManagerFactory, buildSourceBuilder,
                buildResolverDir)
        settingsFactoryMocker = new MockFor(SettingsFactory)
    }

    @After
    public void tearDown() {
        HelperUtil.deleteTestDir()
    }

    @Test public void testSettingsProcessor() {
        assertSame(settingsScriptMetaData, settingsProcessor.settingsScriptMetaData)
        assertSame(scriptProcessorMock, settingsProcessor.scriptProcessor)
        assert settingsProcessor.importsReader.is(importsReader)
        assert settingsProcessor.settingsFactory.is(settingsFactory)
        assert settingsProcessor.dependencyManagerFactory.is(dependencyManagerFactory)
        assert settingsProcessor.buildSourceBuilder.is(buildSourceBuilder)
        assert settingsProcessor.buildResolverDir.is(buildResolverDir)
    }

    @Test public void testCreateBasicSettings() {
        File expectedCurrentDir = new File(TEST_ROOT_DIR, 'currentDir')
        expectedStartParameter = createStartParameter(expectedCurrentDir)
        prepareSettingsFactoryMocker(expectedCurrentDir, expectedCurrentDir)
        settingsFactoryMocker.use(settingsProcessor.settingsFactory) {
            assert settingsProcessor.createBasicSettings(expectedRootFinder, expectedStartParameter).is(expectedSettings)
        }
        assertEquals([], expectedSettings.projectPaths)
        checkBuildResolverDir(buildResolverDir)
    }

    @Test public void testWithNonExistingBuildResolverDir() {
        HelperUtil.deleteTestDir()
        File expectedCurrentDir = new File(TEST_ROOT_DIR, 'currentDir')
        expectedStartParameter = createStartParameter(expectedCurrentDir)
        prepareSettingsFactoryMocker(expectedCurrentDir, expectedCurrentDir)
        settingsFactoryMocker.use(settingsProcessor.settingsFactory) {
            assert settingsProcessor.createBasicSettings(expectedRootFinder, expectedStartParameter).is(expectedSettings)
        }
        assertEquals([], expectedSettings.projectPaths)
        checkBuildResolverDir(buildResolverDir)
    }

    @Test public void testProcessWithCurrentDirAsSubproject() {
        File currentDir = new File(TEST_ROOT_DIR, 'currentDir')
        assertSame(expectedSettings, runCUT(TEST_ROOT_DIR, currentDir, ['currentDir', 'path2'], buildResolverDir))
    }

    @Test public void testProcessWithCurrentDirNoSubproject() {
        File currentDir = new File(TEST_ROOT_DIR, 'currentDir')
        assertSame(expectedSettings, runCUT(TEST_ROOT_DIR, currentDir, ['path1', 'path2'], buildResolverDir) {
            prepareSettingsFactoryMocker(currentDir, currentDir)
        })
    }

    @Test public void testProcessWithCurrentDirAsRootDir() {
        assertSame(expectedSettings, runCUT(TEST_ROOT_DIR, TEST_ROOT_DIR, ['path1', 'path2'], buildResolverDir))
    }

    @Test public void testProcessWithNullBuildResolver() {
        settingsProcessor.buildResolverDir = null
        assertSame(expectedSettings, runCUT(TEST_ROOT_DIR, TEST_ROOT_DIR, ['path1', 'path2'],
                new File(TEST_ROOT_DIR, Project.TMP_DIR_NAME + "/" + DependencyManager.BUILD_RESOLVER_NAME)))
    }

    private void prepareSettingsFactoryMocker(File expectedRootDir, File expectedCurrentDir) {
        expectedSettings.rootFinder = expectedRootFinder
        expectedSettings.startParameter = expectedStartParameter
        settingsFactoryMocker.demand.createSettings(1..1) {DependencyManagerFactory dependencyManagerFactory,
                                                           BuildSourceBuilder buildSourceBuilder, RootFinder rootFinder, StartParameter startParameter ->
            assert dependencyManagerFactory.is(settingsProcessor.dependencyManagerFactory)
            assert buildSourceBuilder.is(settingsProcessor.buildSourceBuilder)
            assert rootFinder.is(expectedRootFinder)
            assertEquals(expectedStartParameter, startParameter)
            expectedSettings
        }

    }

    private DefaultSettings runCUT(File rootDir, File currentDir, List includePaths, File expectedBuildResolverRoot,
                                   Closure customSettingsFactoryPreparation = {}) {
        String expectedScriptAttachement = "expectedScriptAttachement"

        Script expectedScript = new EmptyScript();

        ImportsReader mockImportsReader = [getImports: {File importsRootDir -> expectedScriptAttachement }] as ImportsReader
        settingsProcessor.setImportsReader(mockImportsReader)
            
        File settingsFile = new File(settingsFileDir, 'settingsfile')
        expectedRootFinder.rootDir = rootDir
        String expectedSettingsText = "somesettings"
        GFileUtils.writeStringToFile(settingsFile, expectedSettingsText)
        expectedRootFinder.settingsFile = settingsFile
        expectedStartParameter = createStartParameter(currentDir)
        expectedSettings.setProjectPaths(includePaths)
        prepareSettingsFactoryMocker(rootDir, currentDir)
        customSettingsFactoryPreparation()
        ScriptSource expectedScriptSource = new FileScriptSource("settings file", settingsFile, mockImportsReader);

        context.checking {
            one(scriptProcessorMock).createScript(
                    withParam(ReflectionEqualsMatcher.reflectionEquals(expectedScriptSource)),
                    withParam(Matchers.sameInstance(Thread.currentThread().contextClassLoader)),
                    withParam(Matchers.equalTo(Script.class)))
            will(returnValue(expectedScript))
            one(settingsScriptMetaData).applyMetaData(expectedScript, expectedSettings)
        }

        DefaultSettings settings
        settingsFactoryMocker.use(settingsProcessor.settingsFactory) {
            settings = settingsProcessor.process(expectedRootFinder, expectedStartParameter)
        }
        checkBuildResolverDir(expectedBuildResolverRoot)
        settings
    }

    private void checkBuildResolverDir(File buildResolverDir) {
        assertEquals(buildResolverDir, settingsProcessor.dependencyManagerFactory.buildResolverDir)
        assert !buildResolverDir.exists()
    }

    StartParameter createStartParameter(File currentDir) {
        StartParameter startParameter = StartParameter.newInstance(expectedStartParameter);
        startParameter.setGradleUserHomeDir(new File('gradleUserHomeDir'))
        startParameter.setCurrentDir(currentDir);
        startParameter
    }
}