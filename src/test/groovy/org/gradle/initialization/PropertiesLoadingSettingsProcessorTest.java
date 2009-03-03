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
package org.gradle.initialization;

import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.Expectations;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.gradle.StartParameter;
import org.gradle.api.internal.SettingsInternal;

import java.io.File;

@RunWith(JMock.class)
public class PropertiesLoadingSettingsProcessorTest {
    private final JUnit4Mockery context = new JUnit4Mockery();

    @Test
    public void loadsPropertiesThenDelegatesToBackingSettingsProcessor() {
        final SettingsProcessor delegate = context.mock(SettingsProcessor.class);
        final ISettingsFinder settingsFinder = context.mock(ISettingsFinder.class);
        final IGradlePropertiesLoader propertiesLoader = context.mock(IGradlePropertiesLoader.class);
        final StartParameter startParameter = new StartParameter();
        final SettingsInternal settings = context.mock(SettingsInternal.class);
        final File settingsDir = new File("root");

        PropertiesLoadingSettingsProcessor processor = new PropertiesLoadingSettingsProcessor(delegate);

        context.checking(new Expectations() {{
            allowing(settingsFinder).getSettingsDir();
            will(returnValue(settingsDir));
            one(propertiesLoader).loadProperties(settingsDir, startParameter);
            one(delegate).process(settingsFinder, startParameter, propertiesLoader);
            will(returnValue(settings));
        }});

        assertThat(processor.process(settingsFinder, startParameter, propertiesLoader), sameInstance(settings));
    }
}
