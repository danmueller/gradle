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

package org.gradle.api.internal.dependencies

import org.apache.ivy.core.settings.IvySettings
import org.apache.ivy.plugins.resolver.ChainResolver
import org.apache.ivy.plugins.resolver.IBiblioResolver
import org.gradle.api.DependencyManager
import org.gradle.api.internal.dependencies.SettingsConverter

/**
 * @author Hans Dockter
 */
class SettingsConverterTest extends GroovyTestCase {
    static final IBiblioResolver TEST_RESOLVER = new IBiblioResolver()
    static {
        TEST_RESOLVER.name = 'resolver'
    }

    static final IBiblioResolver TEST_UPLOAD_RESOLVER = new IBiblioResolver()
    static {
        TEST_UPLOAD_RESOLVER.name = 'uploadResolver'
    }

    SettingsConverter converter

    void setUp() {
        converter = new SettingsConverter()
    }
    void testConvert() {
        File testGradleUserHome = new File('gradleUserHome')
        File testBuildResolverDir = new File('testBuildResolverDir')
        IvySettings settings = converter.convert([TEST_RESOLVER], [TEST_UPLOAD_RESOLVER], testGradleUserHome, testBuildResolverDir)
        ChainResolver chainResolver = settings.getResolver(SettingsConverter.CHAIN_RESOLVER_NAME)
        assertEquals(2, chainResolver.resolvers.size())
        assert chainResolver.resolvers[0].name.is(DependencyManager.BUILD_RESOLVER_NAME)
        assertEquals(["$testBuildResolverDir.absolutePath/$DependencyManager.BUILD_RESOLVER_PATTERN"],
                chainResolver.resolvers[0].ivyPatterns)
        assertEquals(["$testBuildResolverDir.absolutePath/$DependencyManager.BUILD_RESOLVER_PATTERN"],
                chainResolver.resolvers[0].artifactPatterns)
        assert chainResolver.resolvers[1].is(TEST_RESOLVER)
        assert settings.getResolver(TEST_UPLOAD_RESOLVER.name).is(TEST_UPLOAD_RESOLVER)
        assertEquals(testGradleUserHome.canonicalPath + '/cache', settings.getVariable('ivy.cache.dir'))

    }

    void testWithGivenSettings() {
        IvySettings ivySettings = [:] as IvySettings
        converter.ivySettings = ivySettings
        assert ivySettings.is(converter.convert([TEST_RESOLVER], [TEST_UPLOAD_RESOLVER], new File(''), new File('')))
    }
}