package org.gradle.api.testing.running;

import org.gradle.api.testing.TestFrameworkRunner;

import java.io.File;

/**
 * @author Tom Eyckmans
 */
public abstract class AbstractForkedTestFrameworkRunner {

    protected AbstractForkedTestFrameworkRunner() {
    }

    protected void execute(String[] args) {

        if ( args.length != 1 ) {
            System.err.println("Need test run option file parameter");
            System.exit(1);
        }
        else {
            final File testRunOptionFile = new File(args[0]);
            if ( testRunOptionFile.exists() && testRunOptionFile.isFile() ) {
                final TestFrameworkRunner actualRunner = getActualRunner();
                
                try {
//                    actualRunner.execute(testRunOptionFile);
                }
                catch ( Throwable t ) {
                    t.printStackTrace();
                    System.exit(2);
                }
            }
            else {
                System.err.println("test run option file "+testRunOptionFile.getAbsolutePath()+" is not a file or doesn't exist");
                System.exit(3);
            }
        }

    }

    protected abstract TestFrameworkRunner getActualRunner();
}
