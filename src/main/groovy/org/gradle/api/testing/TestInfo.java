package org.gradle.api.testing;

import java.io.Serializable;

/**
 * @author Tom Eyckmans
 */
public interface TestInfo extends Serializable {

    String getTestClassName();

}
