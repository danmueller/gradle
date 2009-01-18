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

package org.gradle.api.internal.dependencies.maven.deploy.groovy

import org.gradle.api.dependencies.maven.MavenPom
import org.gradle.api.dependencies.maven.GroovyPomFilterContainer
import org.gradle.util.JUnit4GroovyMockery
import org.hamcrest.BaseMatcher
import org.hamcrest.Matcher
import org.gradle.api.dependencies.maven.PublishFilter
import org.hamcrest.Description
import java.lang.reflect.Proxy
import org.gradle.impl.api.internal.dependencies.maven.deploy.ArtifactPomContainer
import org.gradle.impl.api.internal.dependencies.maven.deploy.ArtifactPom
import org.junit.Before
import org.gradle.api.internal.dependencies.maven.deploy.BasePomFilterContainerTest
import org.junit.Test
import org.gradle.impl.api.internal.dependencies.maven.deploy.BasePomFilterContainer
import static org.junit.Assert.*
import org.junit.runner.RunWith
import org.jmock.integration.junit4.JMock
import org.gradle.impl.api.internal.dependencies.maven.deploy.groovy.DefaultGroovyPomFilterContainer

/**
 * @author Hans Dockter
 */
@RunWith(JMock)
class DefaultGroovyPomFilterContainerTest extends BasePomFilterContainerTest {
    static final String TEST_NAME = "somename"
    DefaultGroovyPomFilterContainer groovyPomFilterContainer


    @Before
    public void setUp() {
        super.setUp()
    }

    protected BasePomFilterContainer createPomFilterContainer() {
        return groovyPomFilterContainer = new DefaultGroovyPomFilterContainer(mavenPomFactoryMock);
    }

    @Test
    public void addFilterWithClosure() {
        Closure closureFilter = {}
        MavenPom pom = groovyPomFilterContainer.addFilter(TEST_NAME, closureFilter)
        assertSame(pomMock, pom);
        assertSame(pomMock, groovyPomFilterContainer.pom(TEST_NAME));
        assertSame(closureFilter, getClosureFromProxy(groovyPomFilterContainer.filter(TEST_NAME)));
    }

    private Closure getClosureFromProxy(PublishFilter filter) {
        Proxy.getInvocationHandler(filter).delegate
    }

    @Test
    public void filterWithClosure() {
        Closure closureFilter = {}
        context.checking {
            one(pomFilterMock).setFilter(withParam(FilterMatcher.equalsFilter(closureFilter)))
        }
        MavenPom pom = groovyPomFilterContainer.filter(closureFilter)
    }

    @Test
    public void defaultPomWithClosure() {
        String testGroup = "testGroup"
        context.checking {
            one(pomFilterMock).getPomTemplate(); will(returnValue(pomMock))
            one(pomMock).setGroupId(testGroup);
        }
        groovyPomFilterContainer.pom {
            groupId = testGroup
        }
    }

    @Test
    public void pomWithClosure() {
        groovyPomFilterContainer.addFilter(TEST_NAME, {})
        String testGroup = "testGroup"
        context.checking {
            one(pomMock).setGroupId(testGroup);
        }
        groovyPomFilterContainer.pom(TEST_NAME) {
            groupId = testGroup
        }
    }
}

public class FilterMatcher extends BaseMatcher {
    Closure filter

    public void describeTo(Description description) {
        description.appendText("matching filter");
    }

    public boolean matches(Object actual) {
        return getClosureFromProxy(actual) == filter;
    }

    private Closure getClosureFromProxy(PublishFilter filter) {
        Proxy.getInvocationHandler(filter).delegate
    }


    @Factory
    public static Matcher<PublishFilter> equalsFilter(Closure filter) {
        return new FilterMatcher(filter: filter);
    }

}

