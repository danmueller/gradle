package org.gradle.api.changedetection.state;

import java.io.File;

/**
 * @author Tom Eyckmans
 */
class DirectoriesStateFileChangeListener extends AbstractStateFileChangeListener {
    private final StateFileUtil stateFileUtil;
    private final DefaultDirectoryStateChangeDetecter directoryStateChangeDetecter;

    DirectoriesStateFileChangeListener(final DefaultDirectoryStateChangeDetecter directoryStateChangeDetecter) {
        super(directoryStateChangeDetecter.getStateFileChangeListenerUtil());
        this.stateFileUtil = directoryStateChangeDetecter.getStateFileUtil();
        this.directoryStateChangeDetecter = directoryStateChangeDetecter;
    }

    /**
     * Directory changed
     *
     * @param oldState
     * @param newState
     */
    public void itemChanged(final StateFileItem oldState, final StateFileItem newState) {
        // check files in directory
        directoryStateChangeDetecter.submitDirectoryStateDigestComparator(
                new DirectoryStateDigestComparator(
                    newState,
                    stateFileUtil, 
                    stateFileChangeListenerUtil));
    }

    File convertStateFileItemToFileOrDirectory(StateFileItem stateFileItem) {
        return stateFileUtil.getDirsStateFileKeyToFile(stateFileItem.getKey());
    }
}
