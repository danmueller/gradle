package org.gradle.api.plugins;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Tom Eyckmans
 */
public class ListJoiningConventionValue<T extends List> extends DerivedConventionValue<T> {
    private final List<ConventionValue<T>> conventionValuesToJoin;

    public ListJoiningConventionValue(ConventionValueName<T> conventionValueName, List<ConventionValue<T>> conventionValuesToJoin) {
        super(conventionValueName);
        this.conventionValuesToJoin = conventionValuesToJoin;
    }

    public T getValue() {
        final List<T> joined = new ArrayList<T>();

        for ( ConventionValue<T> conventionValueToJoin : conventionValuesToJoin ) {
            joined.addAll(conventionValueToJoin.getValue());
        }

        return (T)joined;
    }
}
