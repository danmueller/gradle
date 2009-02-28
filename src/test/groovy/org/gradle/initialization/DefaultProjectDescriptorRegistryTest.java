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

import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNull;import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.*;

import java.io.File;

/**
 * @author Hans Dockter
 */
public class DefaultProjectDescriptorRegistryTest {
    private static final File TEST_DIR = new File("testDir");

    private final DefaultProjectDescriptorRegistry registry = new DefaultProjectDescriptorRegistry();

    @Test
    public void addProjectDescriptor() {
        DefaultProjectDescriptor rootProject = new DefaultProjectDescriptor(null, "testName", TEST_DIR, registry);

        registry.addProject(rootProject);
        assertSame(rootProject, registry.getProject(rootProject.getPath()));
        assertSame(rootProject, registry.getProject(rootProject.getProjectDir()));
    }

    @Test
    public void changeProjectDescriptorPath() {
        DefaultProjectDescriptor project = new DefaultProjectDescriptor(null, "name", TEST_DIR, registry);
        registry.addProject(project);

        registry.changeDescriptorPath(":", ":newPath");
        assertThat(registry.getProject(":"), nullValue());
        assertThat(registry.getProject(":newPath"), sameInstance(project));
    }
}
