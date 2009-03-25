package org.gradle.api.testing.execution.control;

import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.DuplicateRealmException;
import org.gradle.util.BootstrapUtil;
import org.gradle.api.GradleException;

import java.io.File;
import java.net.MalformedURLException;

/**
 * @author Tom Eyckmans
 */
public class TestControlClientMain {
    public static void main(String[] args) {
        final int serverPort = Integer.parseInt(args[0]);

        final ClassWorld world = new ClassWorld();
        try {
            final ClassRealm gradleRealm = world.newRealm("gradle");

            for ( File libJar : BootstrapUtil.getGradleHomeLibClasspath() ) {
                gradleRealm.addConstituent(libJar.getAbsoluteFile().toURL());
            }

            final ClassRealm testingRealm = world.newRealm("testing");

            for ( File libJar : BootstrapUtil.getGradleHomeLibClasspath() ) {
                testingRealm.addConstituent(libJar.getAbsoluteFile().toURL());
            }
            testingRealm.addConstituent(new File("/home/teyckmans/lib/org/gradle/git/gradle/build/test-classes/").getAbsoluteFile().toURL());

            final Class testClass = testingRealm.loadClass("org.gradle.util.GradleVersionTest");

            testClass.newInstance()

        } catch (MalformedURLException e) {
            throw new GradleException("failed to start test control client", e);
        } catch (DuplicateRealmException e) {
            throw new GradleException("failed to start test control client", e);
        }

    }
}
