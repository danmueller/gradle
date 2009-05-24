package org.gradle.api.changedetection.state;

import java.io.File;

/**
 * Used to handle
 * @author Tom Eyckmans
 */
class DirectoryStateFileChangeListener extends AbstractStateFileChangeListener {

    private final File directory;

    DirectoryStateFileChangeListener(final StateFileChangeListenerUtil stateFileChangeListenerUtil, File directory) {
        super(stateFileChangeListenerUtil);
        this.directory = directory;
    }

    protected File convertStateFileItemToFileOrDirectory(StateFileItem stateFileItem) {
        return new File(directory, stateFileItem.getKey());
    }
}
