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

package com.bearded.vigilant;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bearded.common.modules.CloudModule;
import com.bearded.common.modules.Module;

public class BeardedService extends Service {

    private static final String TAG = BeardedService.class.getSimpleName();
    private static long ONE_MINUTE_MS = 60 * 1000l;
    private final Handler mHandler = new Handler();

    /**
     * {@inheritDoc}
     */
    @Override
    public int onStartCommand(@NonNull final Intent intent, final int flags, final int startId) {
        final ModuleManager moduleManager = new ModuleManager(this.getApplicationContext());
        Log.d(TAG, String.format("onBind -> Service %s was binded successfully.", TAG));
        for (final Module module : moduleManager.getAvailableModules()) {
            Log.i(TAG, "onBind -> Found module: " + module);
        }
        pushDataToTheCloudPeriodically(moduleManager);
        return Service.START_STICKY; //Service is restarted if terminates.
    }

    private void pushDataToTheCloudPeriodically(@NonNull final ModuleManager moduleManager) {
        mHandler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        pushAllModuleDataToTheCloud(moduleManager);
                        mHandler.postDelayed(this, ONE_MINUTE_MS);
                    }
                });
    }

    private void pushAllModuleDataToTheCloud(@NonNull final ModuleManager moduleManager) {
        for (final Module module : moduleManager.getAvailableModules()) {
            if (module instanceof CloudModule) {
                pushModuleDataToTheCloud((CloudModule) module);
            }
        }
    }

    private void pushModuleDataToTheCloud(@NonNull final CloudModule module) {
        Log.i(TAG, String.format("pushModuleDataToTheCloud -> pushing data from the module %s to the cloud.", module.getModuleName()));
        module.pushCloudDataToTheCloud();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBinder onBind(@NonNull final Intent intent) {
        return null;
    }
}