package org.gradle.api.testing;

import org.gradle.api.tasks.testing.Test;
import org.gradle.api.Project;

/**
 * @author Tom Eyckmans
 */
public interface TestFrameworkRunner {


    void initialize(Project project, Test testTask);

    void execute(Project project, Test testTask);

    void report(Project project, Test testTask);


}
