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

package org.gradle.integtests

import org.junit.Assert
import org.junit.runner.RunWith
import org.junit.Test

/**
 * @author Hans Dockter
 */
// todo To make this test stronger, we should check against the output of a file appender. Rigth now Gradle does not provided this easily but eventually will.
@RunWith(DistributionIntegrationTestRunner.class)
class LoggingIntegrationTest {
    final static String PREFIX = "276hfe7qlk3sl'aspeie"
    
    // Injected by test runner
    private GradleDistribution dist;
    private GradleExecuter executer;

    @Test
    public void loggingSamples() {
        File loggingDir = new File(dist.samplesDir, 'logging')
        List quietOuts = ['Out', 'Log', 'TaskOut', 'Project2Out']
        List lifecycleOuts = ['TaskOut']
        List infoOuts = ['Out', 'Log', 'TaskOut']
        List debugOuts = ['Log']
        List warnOuts = ['Log']
        List errorOuts = ['Log']
        List allOuts = [quietOuts, warnOuts, lifecycleOuts, infoOuts, debugOuts]
        List allPrefixes = ['quiet', 'warn', 'lifecycle', 'info', 'debug']

        checkOutput(executer.inDirectory(loggingDir).withTasks('log').withArguments('-q').run(),
            errorOuts, allOuts, 0, allPrefixes)
        checkOutput(executer.reset().inDirectory(loggingDir).withTasks('log').withArguments().run(),
            errorOuts, allOuts, 2, allPrefixes)
        checkOutput(executer.reset().inDirectory(loggingDir).withTasks('log').withArguments('-i').run(),
            errorOuts, allOuts, 3, allPrefixes)
        checkOutput(executer.reset().inDirectory(loggingDir).withTasks('log').withArguments('-d').run(),
            errorOuts, allOuts, 4, allPrefixes)
    }

    static void checkOutput(ExecutionResult result, List errorOuts, List allOuts, includedIndex, List allPrefixes) {
        checkOuts(true, result.error, errorOuts, PREFIX + 'error')
        allOuts.eachWithIndex {outList, i ->
            boolean includes = false
            if (i <= includedIndex) {
                includes = true
            }
            checkOuts(includes, result.output, outList, PREFIX + allPrefixes[i]) 
        }
    }


    static void checkOuts(boolean shouldContain, String result, List outs, String prefix) {
        outs.each { expectedOut ->
            Assert.assertEquals(prefix, shouldContain, result.contains(prefix + expectedOut))
        }
    }
}
