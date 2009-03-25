package org.gradle.util;

import java.io.File;

/**
 * @author Tom Eyckmans
 */
public interface FileFilterMatchReceiver {

    void receive(File filterMatch, String filterMatchPath);
}
