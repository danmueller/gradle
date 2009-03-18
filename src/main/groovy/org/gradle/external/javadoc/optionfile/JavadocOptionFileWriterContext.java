package org.gradle.external.javadoc.optionfile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Tom Eyckmans
 */
public class JavadocOptionFileWriterContext {
    private final BufferedWriter writer;

    public JavadocOptionFileWriterContext(BufferedWriter writer) {
        this.writer = writer;
    }

    public JavadocOptionFileWriterContext write(String string) throws IOException {
        writer.write(string);
        return this;
    }

    public JavadocOptionFileWriterContext newLine() throws IOException {
        writer.newLine();
        return this;
    }

    public JavadocOptionFileWriterContext writeOptionHeader(String option) throws IOException {
        write("-");
        write(option);
        write(" ");
        return this;
    }

    public JavadocOptionFileWriterContext writeOption(String option) throws IOException {
        writeOptionHeader(option);
        newLine();
        return this;
    }

    public JavadocOptionFileWriterContext writeValueOption(String option, String value) throws IOException {
        writeOptionHeader(option);
        write("\'");
        write(value);
        write("\'");
        newLine();
        return this;
    }

    public JavadocOptionFileWriterContext writeValueOption(String option, Collection<String> values) throws IOException {
        for ( final String value : values ) {
            writeValueOption(option, value);
        }
        return this;
    }

    public JavadocOptionFileWriterContext writeValuesOption(String option, Collection<String> values, String joinValuesBy) throws IOException {
        writeOptionHeader(option);
        write("\'");
        final Iterator<String> valuesIt = values.iterator();
        while (valuesIt.hasNext()) {
            write(valuesIt.next());
            if (valuesIt.hasNext()) {
                write(joinValuesBy);
            }
        }
        write("\'");
        newLine();
        return this;
    }

    public JavadocOptionFileWriterContext writePathOption(String option, Collection<File> files, String joinValuesBy) throws IOException {
        writeOptionHeader(option);
        write("\'");
        final Iterator<File> filesIt = files.iterator();
        while ( filesIt.hasNext() ) {
            write(filesIt.next().getAbsolutePath());
            if (filesIt.hasNext()) {
                write(joinValuesBy);
            }
        }
        write("\'");
        newLine();
        return this;
    }

}
