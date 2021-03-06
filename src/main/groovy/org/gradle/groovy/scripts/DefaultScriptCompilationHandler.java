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

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import org.codehaus.groovy.control.*;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.gradle.api.GradleException;
import org.gradle.api.GradleScriptException;
import org.gradle.util.Clock;
import org.gradle.util.GFileUtils;
import org.gradle.util.WrapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URLClassLoader;
import java.security.CodeSource;

/**
 * @author Hans Dockter
 */
public class DefaultScriptCompilationHandler implements ScriptCompilationHandler {
    private Logger logger = LoggerFactory.getLogger(DefaultScriptCompilationHandler.class);

    private final CachePropertiesHandler cachePropertiesHandler;
    private final CompilationUnit.SourceUnitOperation transformer;

    public DefaultScriptCompilationHandler(CachePropertiesHandler cachePropertiesHandler) {
        this(cachePropertiesHandler, new CompilationUnit.SourceUnitOperation() {
            @Override
            public void call(SourceUnit source) throws CompilationFailedException {
            }
        });
    }

    public DefaultScriptCompilationHandler(CachePropertiesHandler cachePropertiesHandler,
                                           CompilationUnit.SourceUnitOperation transformer) {
        this.cachePropertiesHandler = cachePropertiesHandler;
        this.transformer = transformer;
    }

    public <T extends Script> T createScriptOnTheFly(ScriptSource source, ClassLoader classLoader,
                                                     Class<T> scriptBaseClass) {
        Clock clock = new Clock();
        CompilerConfiguration configuration = createBaseCompilerConfiguration(scriptBaseClass);
        Class scriptClass = parseScript(source, classLoader, configuration);
        T script = scriptBaseClass.cast(InvokerHelper.createScript(scriptClass, new Binding()));

        logger.debug("Timing: Creating script took: {}", clock.getTime());
        return script;
    }

    public void writeToCache(ScriptSource source, ClassLoader classLoader, File scriptCacheDir,
                             Class<? extends Script> scriptBaseClass) {
        Clock clock = new Clock();
        GFileUtils.deleteDirectory(scriptCacheDir);
        scriptCacheDir.mkdirs();
        CompilerConfiguration configuration = createBaseCompilerConfiguration(scriptBaseClass);
        configuration.setTargetDirectory(scriptCacheDir);
        parseScript(source, classLoader, configuration);

        cachePropertiesHandler.writeProperties(source.getText(), scriptCacheDir);
        logger.debug("Timing: Writing script to cache at {} took: {}", scriptCacheDir.getAbsolutePath(),
                clock.getTime());
    }

    private Class parseScript(ScriptSource source, ClassLoader classLoader, CompilerConfiguration configuration) {
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader(classLoader, configuration, false) {
            @Override
            protected CompilationUnit createCompilationUnit(CompilerConfiguration config, CodeSource source) {
                CompilationUnit compilationUnit = super.createCompilationUnit(config, source);
                compilationUnit.addPhaseOperation(transformer, Phases.CANONICALIZATION);
                return compilationUnit;
            }
        };
        String scriptText = source.getText();
        String scriptName = source.getClassName();
        Class scriptClass;
        try {
            scriptClass = groovyClassLoader.parseClass(scriptText == null ? "" : scriptText, scriptName);
        } catch (MultipleCompilationErrorsException e) {
            throw new GradleScriptException(String.format("Could not compile %s.", source.getDisplayName()), e, source,
                    e.getErrorCollector().getSyntaxError(0).getLine());
        } catch (CompilationFailedException e) {
            throw new GradleException(String.format("Could not compile %s.", source.getDisplayName()), e);
        }
        if (scriptClass == null) {
            // Assume an empty script
            String emptySource = String.format("class %s extends %s { public Object run() { return null } }",
                    source.getClassName(), configuration.getScriptBaseClass().replaceAll("\\$", "."));
            scriptClass = groovyClassLoader.parseClass(emptySource, scriptName);
        }
        return scriptClass;
    }

    private CompilerConfiguration createBaseCompilerConfiguration(Class<? extends Script> scriptBaseClass) {
        CompilerConfiguration configuration = new CompilerConfiguration();
        configuration.setScriptBaseClass(scriptBaseClass.getName());
        return configuration;
    }

    public <T extends Script> T loadFromCache(ScriptSource source, ClassLoader classLoader, File scriptCacheDir,
                                              Class<T> scriptBaseClass) {
        String scriptText = source.getText();
        String scriptName = source.getClassName();
        CachePropertiesHandler.CacheState cacheState = cachePropertiesHandler.getCacheState(scriptText, scriptCacheDir);
        if (cacheState == CachePropertiesHandler.CacheState.INVALID) {
            return null;
        }
        Clock clock = new Clock();
        Script script;
        try {
            URLClassLoader urlClassLoader = new URLClassLoader(WrapUtil.toArray(scriptCacheDir.toURI().toURL()),
                    classLoader);
            script = (Script) urlClassLoader.loadClass(scriptName).newInstance();
        } catch (ClassNotFoundException e) {
            logger.debug("Class not in cache: ", e);
            return null;
        } catch (Exception e) {
            throw new GradleException(e);
        }
        if (!scriptBaseClass.isInstance(script)) {
            return null;
        }
        logger.debug("Timing: Loading script from cache took: {}", clock.getTime());
        return scriptBaseClass.cast(script);
    }

    public CachePropertiesHandler getCachePropertyHandler() {
        return cachePropertiesHandler;
    }
}
