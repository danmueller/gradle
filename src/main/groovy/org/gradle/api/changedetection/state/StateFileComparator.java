package org.gradle.api.changedetection.state;

import java.io.File;
import java.io.IOException;

/**
 * A StateFileComparator compares two state files and notifies a StateFileChangeListener of the changes.
 *
 * @author Tom Eyckmans
 */
class StateFileComparator {

    private final StateFileUtil stateFileUtil;
    private final File oldStateFile;
    private final File newStateFile;

    /**
     * Used to create a state file comparator for a dirs state file.
     *
     * @param stateFileUtil StateFileUtil to use.
     * @param levelIndex directory level index to use.
     */
    StateFileComparator(final StateFileUtil stateFileUtil, final int levelIndex) {
        this(stateFileUtil, stateFileUtil.getDirsStateFilename(levelIndex));
    }

    /**
     * Used to create a state file comparator for a state file name.
     *
     * @param stateFileUtil StateFileUtil to use.
     * @param stateFilename state filename to use.
     */
    StateFileComparator(final StateFileUtil stateFileUtil, final String stateFilename) {
        this.stateFileUtil = stateFileUtil;
        this.oldStateFile = stateFileUtil.getOldDirsStateFile(stateFilename);
        this.newStateFile = stateFileUtil.getNewDirsStateFile(stateFilename);
    }

    /**
     * The compareStateFiles method opens StateFileReaders to the old/new stateFiles. StateFileItems are read from both
     * StateFileReaders. Because the stateFile keys are alphabetically ordered we can easily determine when keys are
     * added/removed. The StateFileChangeListener is notified if ( !prepared ) throw new IllegalArgumentException("not prepared for read yet!");based on StateFileItem availability and key comparison results.  
     *
     * @param stateFileChangeListener Listener to notify about changes (create,change,delete)
     * @return true if the new/old state was different from each other, otherwise false.
     * @throws IOException In case an IO operation fails for one of the state files (new/old).
     */
    boolean compareStateFiles(StateFileChangeListener stateFileChangeListener) throws IOException {
        boolean stateFileChanged = false;

        StateFileReader oldDirectoriesLevelStateReader = null;
        boolean readNextOldItem = true;
        StateFileItem oldItem = null;

        StateFileReader newDirectoriesLevelStateReader = null;
        boolean readNextNewItem = true;
        StateFileItem newItem = null;
        try {
            oldDirectoriesLevelStateReader = stateFileUtil.getStateFileReader(oldStateFile);
            newDirectoriesLevelStateReader = stateFileUtil.getStateFileReader(newStateFile);

            oldDirectoriesLevelStateReader.prepareForRead();
            newDirectoriesLevelStateReader.prepareForRead();

            while ( readNextNewItem || readNextOldItem ) { // in general there is higher likelyhood in projects for more new items then old ones

                if ( readNextOldItem )
                    oldItem = oldDirectoriesLevelStateReader.readStateFileItem();
                if ( readNextNewItem )
                    newItem = newDirectoriesLevelStateReader.readStateFileItem();

                if ( oldItem == null ) { // no more old items
                    if ( newItem == null ) { // no more new items
                        // state file comparison is done
                        readNextOldItem = false;
                        readNextNewItem = false;
                    }
                    else { // there are new items
                        stateFileChanged = true;

                        // item was created
                        stateFileChangeListener.itemCreated(newItem);

                        readNextOldItem = false;
                        readNextNewItem = true;
                    }
                }
                else { // there are old items
                    if ( newItem == null ) { // no more new items
                        stateFileChanged = true;

                        // item was deleted
                        stateFileChangeListener.itemDeleted(oldItem);

                        readNextOldItem = true;
                        readNextNewItem = false;
                    }
                    else { // there is a new item
                        final String oldItemKey = oldItem.getKey();
                        final String newItemKey = newItem.getKey();
                        final int keyComparisonResult = oldItemKey.compareTo(newItemKey);

                        if ( keyComparisonResult == 0 ) { // same item?
                            final String oldItemDigest = oldItem.getDigest();
                            final String newItemDigest = newItem.getDigest();

                            if ( !oldItemDigest.equals(newItemDigest) ) { // item changed?
                                stateFileChanged = true;

                                // item has changed
                                stateFileChangeListener.itemChanged(oldItem, newItem);
                            }
                            // else item has not changed

                            readNextOldItem = true;
                            readNextNewItem = true;
                        }
                        else if ( keyComparisonResult < 0 ) { // old key is before the new key (alphabetically)
                            stateFileChanged = true;

                            // old item was deleted
                            stateFileChangeListener.itemDeleted(oldItem);

                            readNextOldItem = true;
                            readNextNewItem = false;
                        }
                        else if ( keyComparisonResult > 0 ) { // old key is after the new key (alphabetically)
                            stateFileChanged = true;

                            // new item has been created
                            stateFileChangeListener.itemCreated(newItem);

                            readNextOldItem = false;
                            readNextNewItem = true;
                        }
                    }
                }
            }
        }
        finally {
            if ( oldDirectoriesLevelStateReader != null )
                oldDirectoriesLevelStateReader.lastStateFileItemRead();
            if ( newDirectoriesLevelStateReader != null )
                newDirectoriesLevelStateReader.lastStateFileItemRead();
        }

        return stateFileChanged;
    }


}
