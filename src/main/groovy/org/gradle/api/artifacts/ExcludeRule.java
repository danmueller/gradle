/*
 * Copyright 2009 the original author or authors.
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
package org.gradle.api.artifacts;

import java.util.Map;

/**
 * An ExcludeRule is used to describe transitive dependencies that should be excluded when resolving
 * dependencies.
 *
 * @author Hans Dockter
 */
public interface ExcludeRule {
    String GROUP_KEY = "group";
    String MODULE_KEY = "module";

    /**
     * Returns the arguments of an exclude rule. The possible keys for the map are:
     *
     * <ul>
     * <li><code>group</code> - The exact name of the organization or group that should be excluded.
     * <li><code>module</code> - The exact name of the module that should be excluded
     * </ul>
     */
    Map<String, String> getExcludeArgs();
}
