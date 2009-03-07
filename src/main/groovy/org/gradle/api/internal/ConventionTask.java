/*
 * Copyright 2007 the original author or authors.
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

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskValueName;
import org.gradle.api.plugins.ConventionValue;
import org.gradle.api.plugins.ConventionValueName;
import org.gradle.api.plugins.Convention;
import org.gradle.api.plugins.OverwritableConventionValue;

import java.util.Map;
import java.util.List;

/**
 * @author Hans Dockter
 */
public abstract class ConventionTask extends DefaultTask implements IConventionAware {
    private ConventionAwareHelper conventionAwareHelper;
    private final Convention convention;
    
    public ConventionTask(Project project, String name) {
        super(project, name);
        conventionAwareHelper = new ConventionAwareHelper(this);
        conventionAwareHelper.setConvention(project.getConvention());
        this.convention = project.getConvention();
    }

    public <T> Object conventionMapping(TaskValueName<T> taskValueName, ConventionValueName<T> conventionValueName) {

    }

    public <T extends List> Object conventionMapping(TaskValueName<T> taskValueName, ConventionValueName<T>...conventionValueNames) {

    }

    public Task conventionMapping(Map<String, ConventionValue> mapping) {
        return (Task) conventionAwareHelper.conventionMapping(mapping);
    }

//    public Object conventionProperty(String name) {
//        return conventionAwareHelper.getConventionValue(name);
//    }

//    public void setConventionMapping(Map<String, ConventionValue> conventionMapping) {
//        conventionAwareHelper.setConventionMapping(conventionMapping);
//    }

//    public Map<String, ConventionValue> getConventionMapping() {
//        return conventionAwareHelper.getConventionMapping();
//    }

//    public ConventionAwareHelper getConventionAwareHelper() {
//        return conventionAwareHelper;
//    }

//    public void setConventionAwareHelper(ConventionAwareHelper conventionAwareHelper) {
//        this.conventionAwareHelper = conventionAwareHelper;
//    }

//    public Object conv(Object internalValue, String propertyName) {
//        return conventionAwareHelper.getConventionValue(internalValue, propertyName);
//    }

    public  <T> org.gradle.api.plugins.ConventionValue<T> getConventionValue(ConventionValueName<T> valueName) {
        return new OverwritableConventionValue<T>(convention.<T>getConventionValue(valueName));
    }
}
