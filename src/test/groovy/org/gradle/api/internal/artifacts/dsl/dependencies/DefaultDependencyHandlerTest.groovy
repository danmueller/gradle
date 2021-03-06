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
package org.gradle.api.internal.artifacts.dsl.dependencies

import org.gradle.api.artifacts.ClientModule
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import org.gradle.api.internal.artifacts.dsl.dependencies.DefaultDependencyHandler
import org.gradle.api.internal.artifacts.dsl.dependencies.DependencyFactory
import org.gradle.util.ConfigureUtil
import org.gradle.util.JUnit4GroovyMockery
import org.hamcrest.Matchers
import org.jmock.integration.junit4.JMock
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import static org.junit.Assert.assertThat
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.internal.project.IProjectRegistry

/**
 * @author Hans Dockter
 */
@RunWith(org.jmock.integration.junit4.JMock)
class DefaultDependencyHandlerTest {
  private static final String TEST_CONF_NAME = "someConf"

  private JUnit4GroovyMockery context = new JUnit4GroovyMockery()

  private ConfigurationContainer configurationContainerStub = context.mock(ConfigurationContainer)
  private DependencyFactory dependencyFactoryStub = context.mock(DependencyFactory)
  private Configuration configurationMock = context.mock(Configuration)
  private ProjectFinder projectFinderDummy = context.mock(ProjectFinder)

  private DefaultDependencyHandler dependencyHandler = new DefaultDependencyHandler(
          configurationContainerStub, dependencyFactoryStub, projectFinderDummy)

  @Before
  void setUp() {
    context.checking {
      allowing(configurationContainerStub).findByName(TEST_CONF_NAME); will(returnValue(configurationMock))
    }
  }

  @Test
  void pushOneDependency() {
    String someNotation = "someNotation"
    Dependency dependencyDummy = context.mock(Dependency)
    context.checking {
      allowing(dependencyFactoryStub).createDependency(someNotation, null); will(returnValue(dependencyDummy))
      one(configurationMock).addDependency(dependencyDummy);
    }

    assertThat(dependencyHandler."$TEST_CONF_NAME"(someNotation), Matchers.equalTo(dependencyDummy))
  }

  @Test
  void pushOneDependencyWithClosure() {
    String someNotation = "someNotation"
    Closure configureClosure = {}
    Dependency dependencyDummy = context.mock(Dependency)
    context.checking {
      allowing(dependencyFactoryStub).createDependency(someNotation, configureClosure); will(returnValue(dependencyDummy))
      one(configurationMock).addDependency(dependencyDummy);
    }

    assertThat(dependencyHandler."$TEST_CONF_NAME"(someNotation, configureClosure), Matchers.equalTo(dependencyDummy))
  }

  @Test
  void pushMultipleDependencies() {
    String someNotation1 = "someNotation"
    Map someNotation2 = [a: 'b', c: 'd']
    Dependency dependencyDummy1 = context.mock(Dependency, "dep1")
    Dependency dependencyDummy2 = context.mock(Dependency, "dep2")
    context.checking {
      allowing(dependencyFactoryStub).createDependency(someNotation1, null); will(returnValue(dependencyDummy1))
      allowing(dependencyFactoryStub).createDependency(someNotation2, null); will(returnValue(dependencyDummy2))
      one(configurationMock).addDependency(dependencyDummy1);
      one(configurationMock).addDependency(dependencyDummy2);
    }

    dependencyHandler."$TEST_CONF_NAME"(someNotation1, someNotation2)
  }

  @Test
  void pushMultipleDependenciesViaNestedList() {
    String someNotation1 = "someNotation"
    Map someNotation2 = [a: 'b', c: 'd']
    Dependency dependencyDummy1 = context.mock(Dependency, "dep1")
    Dependency dependencyDummy2 = context.mock(Dependency, "dep2")
    context.checking {
      allowing(dependencyFactoryStub).createDependency(someNotation1, null); will(returnValue(dependencyDummy1))
      allowing(dependencyFactoryStub).createDependency(someNotation2, null); will(returnValue(dependencyDummy2))
      one(configurationMock).addDependency(dependencyDummy1);
      one(configurationMock).addDependency(dependencyDummy2);
    }

    dependencyHandler."$TEST_CONF_NAME"([[someNotation1, someNotation2]])
  }

  @Test
  void pushProject() {
    ProjectDependency projectDependency = context.mock(ProjectDependency)
    Map someMapNotation = [:]
    Closure projectDependencyClosure = {
      assertThat("$TEST_CONF_NAME"(project(someMapNotation)), Matchers.equalTo(projectDependency))
    }
    context.checking {
      allowing(dependencyFactoryStub).createProject(projectFinderDummy, someMapNotation, null); will(returnValue(projectDependency))
      one(configurationMock).addDependency(projectDependency);
    }

    ConfigureUtil.configure(projectDependencyClosure, dependencyHandler)
  }

  @Test
  void pushProjectWithConfigureClosure() {
    ProjectDependency projectDependency = context.mock(ProjectDependency)
    Map someMapNotation = [:]
    Closure configureClosure = {}
    Closure projectDependencyClosure = {
      assertThat("$TEST_CONF_NAME"(project(someMapNotation, configureClosure)), Matchers.equalTo(projectDependency))
    }
    context.checking {
      allowing(dependencyFactoryStub).createProject(projectFinderDummy, someMapNotation, configureClosure); will(returnValue(projectDependency))
      one(configurationMock).addDependency(projectDependency);
    }

    ConfigureUtil.configure(projectDependencyClosure, dependencyHandler)
  }

  @Test
  void pushModule() {
    ClientModule clientModule = context.mock(ClientModule)
    String someNotation = "someNotation"
    Closure moduleClosure = {
      assertThat("$TEST_CONF_NAME"(module(someNotation)), Matchers.equalTo(clientModule))
    }
    context.checking {
      allowing(dependencyFactoryStub).createModule(someNotation, null); will(returnValue(clientModule))
      one(configurationMock).addDependency(clientModule);
    }

    ConfigureUtil.configure(moduleClosure, dependencyHandler)
  }

  @Test
  void pushModuleWithConfigureClosure() {
    ClientModule clientModule = context.mock(ClientModule)
    String someNotation = "someNotation"
    Closure configureClosure = {}
    Closure moduleClosure = {
      assertThat("$TEST_CONF_NAME"(module(someNotation, configureClosure)), Matchers.equalTo(clientModule))
    }
    context.checking {
      allowing(dependencyFactoryStub).createModule(someNotation, configureClosure); will(returnValue(clientModule))
      one(configurationMock).addDependency(clientModule);
    }

    ConfigureUtil.configure(moduleClosure, dependencyHandler)
  }

  @Test(expected = MissingMethodException)
  void pushToUnknownConfiguration() {
    String unknownConf = TEST_CONF_NAME + "delta"
    context.checking {
      allowing(configurationContainerStub).findByName(unknownConf); will(returnValue(null))
    }
    dependencyHandler."$unknownConf"("someNotation")
  }

}
