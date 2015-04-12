package com.bearded.vigilant;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bearded.common.modules.Module;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * (C) Copyright 2015 Xavier Fernández Salas (xavier.fernandez.salas@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *      Xavier Fernández Salas (xavier.fernandez.salas@gmail.com)
 */
@EBean(scope = EBean.Scope.Singleton)
public class ModuleManager {

    private static final String TAG = ModuleManager.class.getSimpleName();

    @NonNull
    private final List<Module> mModules = new ArrayList<>();

    @RootContext
    Context mContext;

    public ModuleManager() {
        Log.d(TAG, String.format("Constructor(Context) -> Initializing modules for flavor %s.", BuildConfig.FLAVOR));
        loadFlavorModules();
    }

    /**
     * Obtains a read-only list with all the loaded modules.
     *
     * @return {@link java.util.List} with all the loaded {@link com.bearded.common.modules.Module}
     */
    @NonNull
    public List<Module> getAvailableModules() {
        return Collections.unmodifiableList(mModules);
    }

    /**
     * Loads and instantiates all the modules from the running flavor into memory.
     */
    private void loadFlavorModules() {
        final String[] flavorModules = mContext.getResources().getStringArray(R.array.modules_list);
        Log.i(TAG, String.format("loadFlavorModules -> Loading %d from flavor %s.", flavorModules.length, BuildConfig.FLAVOR));
        for (final String moduleClassLocation : flavorModules) {
            final Module loadedModule = loadModule(moduleClassLocation);
            if (loadedModule != null) {
                mModules.add(loadModule(moduleClassLocation));
            }
        }
    }

    /**
     * Loads and instantiates a given module into memory.
     *
     * @param moduleClassLocation of the module we want to instantiate.
     * @return {@link com.bearded.common.modules.Module} if it was loaded succesfully - <code>null</code> otherwise.
     */
    @Nullable
    private Module loadModule(@NonNull final String moduleClassLocation) {
        Log.d(TAG, String.format("loadModule -> Loading module %s.", moduleClassLocation));
        final Class<?> moduleClass;
        try {
            moduleClass = Class.forName(moduleClassLocation);
        } catch (final ClassNotFoundException e) {
            Log.e(TAG, String.format("loadModule -> When obtaining the module class %s the following error was thrown ->", moduleClassLocation), e);
            return null;
        }
        final Constructor moduleConstructor;
        try {
            moduleConstructor = moduleClass.getDeclaredConstructor(Context.class);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, String.format("loadModule -> The constructor (Context) was not found in module: %s", moduleClassLocation));
            return null;
        }
        final Module module;
        try {
            module = (Module) moduleConstructor.newInstance(mContext);
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Log.e(TAG, String.format("loadModule -> The following error was thrown when instantiating the module %s -> ", moduleClassLocation), e);
            return null;
        }
        return module;
    }
}