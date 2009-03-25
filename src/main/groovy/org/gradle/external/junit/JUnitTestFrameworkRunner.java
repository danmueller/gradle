package org.gradle.external.junit;

import org.gradle.api.testing.running.AbstractTestFrameworkRunner;
import org.gradle.api.Project;
import org.gradle.api.tasks.testing.Test;

/**
 * @author Tom Eyckmans
 */
class JUnitTestFrameworkRunner extends AbstractTestFrameworkRunner {

   /* public void execute(File testRunOptionFile) {
        final JUnitCore junit = new JUnitCore();

        junit.run();
    }*/
   public void initialize(Project project, Test testTask) {
       //To change body of implemented methods use File | Settings | File Templates.
   }

    public void execute(Project project, Test testTask) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void report(Project project, Test testTask) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
