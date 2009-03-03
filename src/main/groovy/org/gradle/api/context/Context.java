package org.gradle.api.context;

import org.gradle.api.plugins.ConventionValueName;
import org.gradle.api.plugins.ConventionValue;

/**
 * @author Tom Eyckmans
 */
public interface Context {

    Context getParentContext();

    Object getContextedItem();

    <T> void addContextValue(ConventionValueName<T> name, ConventionValue<T> value);

    <T> ConventionValue<T> getContextValue(ConventionValueName<T> name);

}
