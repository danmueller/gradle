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

import org.apache.ivy.core.IvyPatternHelper
import org.gradle.api.DependencyManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Hans Dockter
 */
class BuildSourceBuilder {
    Logger logger = LoggerFactory.getLogger(BuildSourceBuilder)

    static final BUILD_SRC_ORG = 'org.gradle'
    static final BUILD_SRC_MODULE = 'buildSrc'
    static final BUILD_SRC_REVISION = 'SNAPSHOT'
    static final BUILD_SRC_TYPE = 'jar'
    static final BUILD_SRC_ID = "$BUILD_SRC_ORG:$BUILD_SRC_MODULE:$BUILD_SRC_REVISION"

    static final DEFAULT_SCRIPT = '''
usePlugin('groovy')
sourceCompatibility = 1.5
targetCompatibility = 1.5
'''

    EmbeddedBuildExecuter embeddedBuildExecuter

    BuildSourceBuilder() {}

    BuildSourceBuilder(EmbeddedBuildExecuter embeddedBuildExecuter) {
        this.embeddedBuildExecuter = embeddedBuildExecuter
    }

    def createDependency(File buildSrcDir, File buildResolverDir, String buildScriptName,
                         List taskNames, Map projectProperties, Map systemProperties,
                         boolean recursive, boolean searchUpwards) {
        assert buildSrcDir && buildScriptName && buildResolverDir

        logger.debug('Starting to build the build sources.')
        if (!buildSrcDir.isDirectory()) {
            logger.debug('Build source dir does not exists!. We leave.')
            return null
        }
        if (!taskNames) {
            logger.debug('No task names specified. We leave..')
            return null
        }
        logger.info(('=' * 50) + ' Start building buildSrc')
        Map allProjectProperties = projectProperties + dependencyProjectProps
        if (!new File(buildSrcDir, buildScriptName).isFile()) {
            logger.debug('Build script file does not exists. Using default one.')
            embeddedBuildExecuter.executeEmbeddedScript(buildResolverDir, buildSrcDir, DEFAULT_SCRIPT, taskNames, allProjectProperties,
                    systemProperties)
        } else {
            embeddedBuildExecuter.execute(buildResolverDir, buildSrcDir, buildScriptName, taskNames, allProjectProperties, systemProperties,
                    recursive, searchUpwards)
        }
        logger.info("Check if build artifact exists: ${buildArtifactFile(buildResolverDir)}")
        if (!buildArtifactFile(buildResolverDir).exists()) {
            logger.info('Building buildSrc has not produced any artifact!')
            return null
        }
        logger.info(('=' * 50) + ' Finished building buildSrc')
        BUILD_SRC_ID
    }

    File buildArtifactFile(File buildResolverDir) {
        String path = IvyPatternHelper.substitute("$buildResolverDir.absolutePath/$DependencyManager.BUILD_RESOLVER_PATTERN",
                BUILD_SRC_ORG, BUILD_SRC_MODULE, BUILD_SRC_REVISION, BUILD_SRC_MODULE, BUILD_SRC_TYPE, BUILD_SRC_TYPE)
        new File(path)
    }

    private Map getDependencyProjectProps() {
        [group: BuildSourceBuilder.BUILD_SRC_ORG,
                version: BuildSourceBuilder.BUILD_SRC_REVISION,
                type: 'jar']
    }

}