package org.gradle.api;

import org.gradle.groovy.scripts.ScriptSource;
import static org.hamcrest.Matchers.*;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(org.jmock.integration.junit4.JMock.class)
public class GradleExceptionTest {
    private final JUnit4Mockery context = new JUnit4Mockery();
    private final StackTraceElement element = new StackTraceElement("class", "method", "filename", 7);
    private final ScriptSource source = context.mock(ScriptSource.class);

    @Before
    public void setUp() {
        context.checking(new Expectations() {{
            allowing(source).getClassName();
            will(returnValue("filename"));
            allowing(source).getDescription();
            will(returnValue("<description>"));
        }});
    }

    @Test
    public void extractsLineNumbersFromStackTrace() {

        GradleException exception = new GradleException("<message>", null, source);
        exception.setStackTrace(new StackTraceElement[]{element});
        assertThat(exception.getMessage(), equalTo(String.format("<description> at line(s): 7%n<message>")));
    }

    @Test
    public void extractsLineNumbersFromStackTraceOfCause() {
        RuntimeException cause = new RuntimeException();
        cause.setStackTrace(new StackTraceElement[]{element});
        GradleException exception = new GradleException("<message>", cause, source);
        assertThat(exception.getMessage(), equalTo(String.format("<description> at line(s): 7%n<message>")));
    }

    @Test
    public void messageIndicatesWhenNoLineNumbersFound() {
        GradleException exception = new GradleException("<message>", null, source);
        assertThat(exception.getMessage(), equalTo(String.format("<description> No line info available from stacktrace.%n<message>")));
    }
}