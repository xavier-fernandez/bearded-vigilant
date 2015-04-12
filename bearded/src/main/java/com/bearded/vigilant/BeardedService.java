package com.bearded.vigilant;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bearded.common.modules.Module;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;

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
@EService
public class BeardedService extends Service {

    private static final String TAG = BeardedService.class.getSimpleName();

    @Bean
    ModuleManager mModuleManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public IBinder onBind(@NonNull final Intent intent) {
        Log.d(TAG, String.format("onBind -> Service %s was binded successfully.", TAG));
        for (final Module module : mModuleManager.getAvailableModules()){
            Log.i(TAG, "onBind -> Found module: " + module);
        }
        return null;
    }
}