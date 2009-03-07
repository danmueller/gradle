/*
 * Copyright 2007-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal;

import org.gradle.api.plugins.ConventionValueName;
import org.gradle.api.tasks.TaskValueName;

import java.util.Map;
import java.util.List;

/**
 * @author Hans Dockter
 */
public interface IConventionAware {
    <T> Object conventionMapping(TaskValueName<T> taskValueName, ConventionValueName<T> conventionValueName);

    <T extends List> Object conventionMapping(TaskValueName<T> taskValueName, ConventionValueName<T>...conventionValueNames);

    <T> T conventionProperty(ConventionValueName<T> name);

//    void setConventionMapping(Map<String, ConventionValue> conventionMapping);

//    Map<String, ConventionValue> getConventionMapping();
    
    /**
     * Returns a value for a property of a convention aware object. If the internal value is different to null,
     * this method returns the internal value. Otherwise the value from the convention mapping for this property is
     * returned. If no such mapping exists for this property, null is returned.
     *
     * @param internalValue The internal value of the property
     * @param propertyName The name of the propery of the convention aware object
     * @return The value of a property
     */
//    Object conv(Object internalValue, String propertyName);

    /**
     *
     * @param valueName
     * @param <T>
     * @return
     */
    <T> org.gradle.api.plugins.ConventionValue<T> getConventionValue(ConventionValueName<T> valueName);
}
