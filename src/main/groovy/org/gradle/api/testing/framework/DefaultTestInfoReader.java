package org.gradle.api.testing.framework;

import org.gradle.api.testing.TestFrameworkTestInfoReader;
import org.gradle.api.testing.TestInfo;
import org.gradle.api.testing.AbstractTestInfo;
import org.gradle.api.GradleException;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Tom Eyckmans
 */
public class DefaultTestInfoReader implements TestFrameworkTestInfoReader {
    private static final char SEPARATOR = ':';

    private final BufferedReader inputReader;

    public DefaultTestInfoReader(BufferedReader inputReader) {
        if ( inputReader == null ) throw new IllegalArgumentException("inputReader is null!");

        this.inputReader = inputReader;
    }


    public TestInfo read() throws IOException {
        TestInfo readTestInfo = null;

        final String inputLine = inputReader.readLine();

        if ( inputLine != null ) {
            final int separatorIndex = inputLine.indexOf(SEPARATOR);

            final String testClassName = inputLine.substring(0, separatorIndex);
            final String testInfoClassName = inputLine.substring(separatorIndex+1);

            try {
                final Class<? extends AbstractTestInfo> testInfoClass = (Class<? extends AbstractTestInfo>)Class.forName(testInfoClassName);
                final Constructor<? extends AbstractTestInfo> testInfoClassConstructor = testInfoClass.getConstructor(String.class);

                readTestInfo = testInfoClassConstructor.newInstance(testClassName);
            }
            catch ( ClassNotFoundException e ) {
                throw new GradleException("failed to read TestInfo", e);
            }
            catch (IllegalAccessException e) {
                throw new GradleException("failed to read TestInfo", e);
            }
            catch (InstantiationException e) {
                throw new GradleException("failed to read TestInfo", e);
            }
            catch (NoSuchMethodException e) {
                throw new GradleException("failed to read TestInfo", e);
            }
            catch (InvocationTargetException e) {
                throw new GradleException("failed to read TestInfo", e);
            }
        }

        return readTestInfo;
    }
}
