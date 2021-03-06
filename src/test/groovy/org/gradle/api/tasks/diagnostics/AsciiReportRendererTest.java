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
package org.gradle.api.tasks.diagnostics;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.util.HelperUtil;
import org.gradle.util.Matchers;
import static org.gradle.util.Matchers.*;
import static org.hamcrest.Matchers.*;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.Expectations;

import java.io.StringWriter;

@RunWith(JMock.class)
public class AsciiReportRendererTest {
    private final JUnit4Mockery context = new JUnit4Mockery();
    private final StringWriter writer = new StringWriter();
    private final AsciiReportRenderer renderer = new AsciiReportRenderer(writer);
    private final Project project = HelperUtil.createRootProject();

    @Test
    public void writesMessageWhenProjectHasNoConfigurations() {
        renderer.startProject(project);
        renderer.completeProject(project);

        assertThat(writer.toString(), containsLine("No configurations"));
    }

    @Test
    public void writesConfigurationHeader() {
        final Configuration configuration = context.mock(Configuration.class);
        context.checking(new Expectations(){{
            allowing(configuration).getName();
            will(returnValue("configName"));
            allowing(configuration).getDescription();
            will(returnValue("description"));
        }});

        renderer.startProject(project);
        renderer.startConfiguration(configuration);
        renderer.completeConfiguration(configuration);
        renderer.completeProject(project);

        assertThat(writer.toString(), containsLine("configName - description"));
        assertThat(writer.toString(), not(containsLine("No configurations")));
    }
}
