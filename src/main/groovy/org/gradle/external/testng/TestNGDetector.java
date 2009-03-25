package org.gradle.external.testng;

import org.gradle.api.testing.execution.AbstractTestFrameworkDetector;
import org.gradle.api.testing.execution.TestClassVisitor;
import org.gradle.api.GradleException;
import org.objectweb.asm.ClassReader;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.List;

/**
 * @author Tom Eyckmans
 */
class TestNGDetector extends AbstractTestFrameworkDetector<TestNGTestClassDetecter> {
    private static final Logger logger = LoggerFactory.getLogger(TestNGDetector.class);

    TestNGDetector(File testClassesDirectory, List<File> testClasspath) {
        super(testClassesDirectory, testClasspath);
    }

    protected TestNGTestClassDetecter createClassVisitor() {
        return new TestNGTestClassDetecter(this);
    }

    public boolean processPossibleTestClass(File testClassFile) {
        final TestClassVisitor classVisitor = createClassVisitor();

        InputStream classStream = null;
        try {
            classStream = new BufferedInputStream(new FileInputStream(testClassFile));
            final ClassReader classReader = new ClassReader(classStream);
            classReader.accept(classVisitor, true);
        }
        catch ( Throwable e ) {
            throw new GradleException("failed to read class file " + testClassFile.getAbsolutePath(), e);
        }
        finally {
            IOUtils.closeQuietly(classStream);
        }

        boolean isTest = classVisitor.isTest();

        if (!isTest) {
            final String superClassName = classVisitor.getSuperClassName();
            if ( superClassName.startsWith("java/lang") ||
                 superClassName.startsWith("groovy/lang") ) {
                isTest = false;
            }
            else {
                final File superClassFile = getSuperTestClassFile(superClassName);
                if ( superClassFile != null ) {
                    isTest = processPossibleTestClass(superClassFile);
                }
                else
                    logger.debug("test-class-scan : failed to scan parent class {}, could not find the class file", superClassName);
            }
        }

        if ( isTest && !classVisitor.isAbstract() )
            testClassNames.add(classVisitor.getClassName() + ".class");

        return isTest;
    }

    /*protected TestInfo createTestInfo(TestSuite testSuite, File testClassFile, TestNGTestClassDetecter classVisitor) {
        TestInfo testInfo = null;

        if ( !classVisitor.isAbstract() ) {
            final TestNGTestClassType testClassType = classVisitor.getTestClassType();
            if ( testClassType == null ) {
                final File superClassFile = getSuperTestClassFile(testSuite, classVisitor.getSuperClassName());
                if ( superClassFile != null )
                    testInfo = isTest(testSuite, superClassFile);
            }
            else {
                switch(testClassType) {
                    case TESTCASE:
                        testInfo = new TestNGTestClassTestInfo(classVisitor.getClassName());
                        break;
                    default:
                        throw new GradleException("encounterd unsupported test class type " + testClassType);
                }
            }
        }

        return testInfo;
    }*/
}
