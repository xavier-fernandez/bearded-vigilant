package com.bearded.vigilant;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bearded.common.modules.Module;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleManager {

    private static final String TAG = ModuleManager.class.getSimpleName();

    @NonNull
    private final List<Module> mModules = new ArrayList<>();

    public ModuleManager(@NonNull Context context) {
        Log.d(TAG, String.format("Constructor(Context) -> Initializing modules for flavor %s.", BuildConfig.FLAVOR));
        loadFlavorModules(context);
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
    private void loadFlavorModules(@NonNull Context context) {
        final String[] flavorModules = context.getResources().getStringArray(R.array.modules_list);
        Log.i(TAG, String.format("loadFlavorModules -> Loading %d from flavor %s.", flavorModules.length, BuildConfig.FLAVOR));
        for (final String moduleClassLocation : flavorModules) {
            final Module loadedModule = loadModule(context, moduleClassLocation);
            if (loadedModule != null) {
                mModules.add(loadedModule);
            }
        }
    }

    /**
     * Loads and instantiates a given module into memory.
     *
     * @param moduleClassLocation of the module we want to instantiate.
     * @return {@link com.bearded.common.modules.Module} if it was loaded successfully - <code>null</code> otherwise.
     */
    @Nullable
    private Module loadModule(@NonNull Context context, @NonNull String moduleClassLocation) {
        Log.d(TAG, String.format("loadModule -> Loading module %s.", moduleClassLocation));
        final Class<?> moduleClass;
        try {
            moduleClass = Class.forName(moduleClassLocation);
        } catch (ClassNotFoundException e) {
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
            module = (Module) moduleConstructor.newInstance(context);
        } catch (InstantiationException ie) {
            Log.e(TAG, String.format("loadModule -> The following error instantiation exception was thrown when instantiating the module %s -> ", moduleClassLocation), ie);
            return null;
        } catch (IllegalAccessException ia) {
            Log.e(TAG, String.format("loadModule -> The following error illegal access exception was thrown when instantiating the module %s -> ", moduleClassLocation), ia);
            return null;
        } catch (InvocationTargetException e) {
            Log.e(TAG, String.format("loadModule -> The following invocation target exception was thrown when instantiating the module %s -> ", moduleClassLocation), e);
            return null;
        }
        return module;
    }
}