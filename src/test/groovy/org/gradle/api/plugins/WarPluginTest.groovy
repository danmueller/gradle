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
 
package org.gradle.api.plugins

import org.gradle.api.Project
import org.gradle.api.internal.project.PluginRegistry
import org.gradle.util.HelperUtil
import static org.gradle.util.WrapUtil.*
import static org.hamcrest.Matchers.*
import static org.junit.Assert.*
import org.junit.Test

/**
 * @author Hans Dockter
 */
class WarPluginTest {
    @Test public void testApply() {
        // todo Make test stronger
        Project project = HelperUtil.createRootProject(new File('path', 'root'))
        WarPlugin warPlugin = new WarPlugin()
        warPlugin.apply(project, new PluginRegistry(), [:])

        def configuration = project.dependencies.configurations[JavaPlugin.COMPILE]
        assertThat(configuration.extendsFrom, equalTo(toSet(WarPlugin.PROVIDED_COMPILE)))
        assertFalse(configuration.visible)
        assertFalse(configuration.transitive)

        configuration = project.dependencies.configurations[JavaPlugin.RUNTIME]
        assertThat(configuration.extendsFrom, equalTo(toSet(JavaPlugin.COMPILE, WarPlugin.PROVIDED_RUNTIME)))
        assertFalse(configuration.visible)
        assertTrue(configuration.transitive)

        configuration = project.dependencies.configurations[WarPlugin.PROVIDED_COMPILE]
        assertThat(configuration.extendsFrom, equalTo(toSet()))
        assertFalse(configuration.visible)
        assertTrue(configuration.transitive)

        configuration = project.dependencies.configurations[WarPlugin.PROVIDED_RUNTIME]
        assertThat(configuration.extendsFrom, equalTo(toSet(WarPlugin.PROVIDED_COMPILE)))
        assertFalse(configuration.visible)
        assertTrue(configuration.transitive)
    }
}
