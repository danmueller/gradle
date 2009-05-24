package org.gradle.api.changedetection.state;

import java.io.File;

/**
 * @author Tom Eyckmans
 */
abstract class AbstractStateFileChangeListener implements StateFileChangeListener {
    protected final StateFileChangeListenerUtil stateFileChangeListenerUtil;

    protected AbstractStateFileChangeListener(StateFileChangeListenerUtil stateFileChangeListenerUtil) {
        if ( stateFileChangeListenerUtil == null ) throw new IllegalArgumentException("stateFileChangeListenerUtil is null!");
        this.stateFileChangeListenerUtil = stateFileChangeListenerUtil;
    }

    /**
     * File or directory created
     *
     * @param createdItem
     */
    public final void itemCreated(final StateFileItem createdItem) {
        final File fileOrDirectory = convertStateFileItemToFileOrDirectory(createdItem);

        stateFileChangeListenerUtil.produceCreatedItemEvent(fileOrDirectory, createdItem);
    }

    /**
     * File or directory deleted
     *
     * @param deletedItem
     */
    public final void itemDeleted(final StateFileItem deletedItem) {
        final File subDirectory = convertStateFileItemToFileOrDirectory(deletedItem);

        stateFileChangeListenerUtil.produceDeletedItemEvent(subDirectory, deletedItem);
    }

    /**
     * File or directory changed
     *
     * @param oldState
     * @param newState
     */
    public void itemChanged(final StateFileItem oldState, final StateFileItem newState) {
        final File subDirectory = convertStateFileItemToFileOrDirectory(oldState);

        stateFileChangeListenerUtil.produceChangedItemEvent(subDirectory, oldState, newState);
    }

    abstract File convertStateFileItemToFileOrDirectory(final StateFileItem stateFileItem);
}
