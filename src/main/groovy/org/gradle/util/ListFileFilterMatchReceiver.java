package org.gradle.util;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Tom Eyckmans
 */
public class ListFileFilterMatchReceiver implements FileFilterMatchReceiver {
    private final List<File> filterMatches;

    public ListFileFilterMatchReceiver() {
        this(new ArrayList<File>());
    }

    public ListFileFilterMatchReceiver(List<File> filterMatches) {
        if ( filterMatches == null ) throw new IllegalArgumentException("filterMatches == null!");
        this.filterMatches = filterMatches;
    }

    public void receive(File filterMatch, String filterMatchPath) {
        filterMatches.add(filterMatch);
    }

    public List<File> getFilterMatches() {
        return filterMatches;
    }
}
