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
package org.gradle.groovy.scripts;

import groovy.lang.Script;
import org.gradle.CacheUsage;
import org.gradle.api.Project;

import java.io.File;

/**
 * @author Hans Dockter
 */
public class DefaultScriptProcessor implements IScriptProcessor {
    private ScriptCompilationHandler scriptCompilationHandler;
    private final CacheUsage cacheUsage;

    public DefaultScriptProcessor(ScriptCompilationHandler scriptCompilationHandler, CacheUsage cacheUsage) {
        this.scriptCompilationHandler = scriptCompilationHandler;
        this.cacheUsage = cacheUsage;
    }

    public <T extends ScriptWithSource> T createScript(ScriptSource source, ClassLoader classLoader,
                                                       Class<T> scriptBaseClass) {
        File sourceFile = source.getSourceFile();
        T script;
        if (isCacheable(sourceFile)) {
            script = loadViaCache(source, classLoader, scriptBaseClass);
        }
        else {
            script = loadWithoutCache(source, classLoader, scriptBaseClass);
        }
        script.setScriptSource(source);
        return script;
    }

    private <T extends ScriptWithSource> T loadWithoutCache(ScriptSource source, ClassLoader classLoader, Class<T> scriptBaseClass) {
        return scriptCompilationHandler.createScriptOnTheFly(source, classLoader, scriptBaseClass);
    }

    private <T extends ScriptWithSource> T loadViaCache(ScriptSource source, ClassLoader classLoader, Class<T> scriptBaseClass) {
        File sourceFile = source.getSourceFile();
        File cacheDir = new File(sourceFile.getParentFile(), Project.CACHE_DIR_NAME);
        File scriptCacheDir = new File(cacheDir, sourceFile.getName());
        if (cacheUsage == CacheUsage.ON) {
            T cachedScript = scriptCompilationHandler.loadFromCache(source, classLoader, scriptCacheDir, scriptBaseClass);
            if (cachedScript != null) {
                return cachedScript;
            }
        }
        scriptCompilationHandler.writeToCache(source, classLoader, scriptCacheDir, scriptBaseClass);
        return scriptCompilationHandler.loadFromCache(source, classLoader, scriptCacheDir, scriptBaseClass);
    }

    private boolean isCacheable(File sourceFile) {
        return cacheUsage != CacheUsage.OFF && sourceFile != null && sourceFile.isFile();
    }

    public ScriptCompilationHandler getScriptCacheHandler() {
        return scriptCompilationHandler;
    }

    public CacheUsage getCacheUsage() {
        return cacheUsage;
    }
}
