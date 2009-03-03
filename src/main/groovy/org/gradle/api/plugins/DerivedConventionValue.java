package org.gradle.api.plugins;

/**
 * @author Tom Eyckmans
 */
public abstract class DerivedConventionValue<T> implements ConventionValue<T> {
    protected final ConventionValueName<T> name;

    public DerivedConventionValue(ConventionValueName<T> name) {
        this.name = name;
    }

    public ConventionValueName<T> getName() {
        return name;
    }

    public abstract T getValue();

    public void setValue(T value) {
        throw new UnsupportedOperationException("setValue not allowed on DerivedConventionValue");
    }
}
