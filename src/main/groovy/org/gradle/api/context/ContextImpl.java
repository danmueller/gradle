package org.gradle.api.context;

import org.gradle.api.plugins.ConventionValue;
import org.gradle.api.plugins.ConventionValueName;
import org.gradle.api.plugins.DerivedConventionValue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tom Eyckmans
 */
public class ContextImpl implements Context {

    private final Context parentContext;
    private final Object contextedItem;
    private final Map<ConventionValueName<?>, ConventionValue<?>> contextValues = new ConcurrentHashMap<ConventionValueName<?>, ConventionValue<?>>();

    public ContextImpl(final Context parentContext, final Object contextedItem) {
        this.parentContext = parentContext;
        this.contextedItem = contextedItem;
    }

    public Context getParentContext() {
        return parentContext;
    }

    public Object getContextedItem() {
        return contextedItem;
    }

    public <T> void addContextValue(ConventionValueName<T> name, ConventionValue<T> value) {
        contextValues.put(name, value);
    }

    public <T> ConventionValue<T> getContextValue(ConventionValueName<T> valueName) {
        return new ContextConventionValue(valueName, this); // delay actual value retrieval 
    }

    private class ContextConventionValue<T> extends DerivedConventionValue<T> {
        private final Context context;

        public ContextConventionValue(ConventionValueName<T> name, Context context) {
            super(name);
            this.context = context;
        }

        public T getValue() {
            ConventionValue<T> contextValue = (ConventionValue<T>)contextValues.get(name);

            if ( contextValue == null && parentContext != null ) {
                contextValue = parentContext.getContextValue(name);
            }

            if ( contextValue == null )
                return null;
            else
                return contextValue.getValue();
        }
    }
}
