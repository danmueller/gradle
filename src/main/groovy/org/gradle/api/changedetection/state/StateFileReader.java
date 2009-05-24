package org.gradle.api.changedetection.state;

import org.apache.commons.io.IOUtils;
import org.gradle.api.GradleException;
import org.gradle.api.io.IoFactory;

import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * A StateFileReader reads keys and digests from a state file. If the state file doesn't exists it simulates reading
 * from an empty state file.
 * 
 * @author Tom Eyckmans
 */
class StateFileReader {
    private final IoFactory ioFactory;
    private final File stateFile;

    private boolean prepared = false;
    private BufferedReader stateFileReader;

    StateFileReader(final IoFactory ioFactory, final File stateFile) {
        if ( ioFactory == null ) throw new IllegalArgumentException("ioFactory is null!");
        if ( stateFile == null ) throw new IllegalArgumentException("stateFile is null!");

        this.ioFactory = ioFactory;
        this.stateFile = stateFile;
    }

    /**
     * Opens a reader on the stateFile if the stateFile exists.
     *
     * @throws IOException When creation of the reader fails.
     */
    void prepareForRead() throws IOException {
        if ( stateFile.exists() && stateFile.isFile() ) {
            stateFileReader = ioFactory.createBufferedReader(stateFile);
        }
        prepared = true;
    }

    /**
     * Method for test puposes.
     *
     * Should only be called after prepareForRead.
     *
     * @return true if the stateFileReader is null.
     */
    boolean isSimulating() {
        if ( !prepared ) throw new IllegalStateException("not yet prepared!");
        return stateFileReader == null;
    }

    /**
     * Reads the next StateFileItem or returns null when the state file is exhausted.
     *
     * @return The next StateFileItem.
     * @throws IOException When reading for the state file reader fails.
     */
    StateFileItem readStateFileItem() throws IOException {
        if ( !prepared ) throw new IllegalStateException("not yet prepared!");
        if ( isSimulating() )
            return null;
        else {
            final String key = stateFileReader.readLine();
            if ( key == null )
                return null;

            final String digest = stateFileReader.readLine();
            if ( digest == null )
                throw new GradleException("state file invalid key ("+key+") did not have a digest value!");
            else
                return new StateFileItem(key, digest);
        }
    }

    void lastStateFileItemRead() {
        IOUtils.closeQuietly(stateFileReader);
    }
}
