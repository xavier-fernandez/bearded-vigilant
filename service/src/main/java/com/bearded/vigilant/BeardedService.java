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

    @NonNull
    private static final String TAG = BeardedService.class.getSimpleName();
    private static long ONE_MINUTE_MS = 60 * 1000l;
    @NonNull
    private final Handler mHandler = new Handler();

    /**
     * {@inheritDoc}
     */
    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId) {
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

    private void pushAllModuleDataToTheCloud(@NonNull ModuleManager moduleManager) {
        for (final Module module : moduleManager.getAvailableModules()) {
            if (module instanceof CloudModule) {
                pushModuleDataToTheCloud((CloudModule) module);
            }
        }
    }

    private void pushModuleDataToTheCloud(@NonNull CloudModule module) {
        Log.i(TAG, String.format("pushModuleDataToTheCloud -> pushing data from the module %s to the cloud.", module.getModuleName()));
        module.pushCloudDataToTheCloud();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        return null;
    }
}