package com.bearded.modules.sensor.light;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

import static android.hardware.Sensor.TYPE_LIGHT;
import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

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
class LightSensorManager implements SensorEventListener {

    private static final String TAG = LightSensorManager.class.getSimpleName();
    @SystemService
    SensorManager mSensorManager;
    @Nullable
    private Sensor mLightSensor;

    public LightSensorManager() {
        if (hasLightSensor()) {
            mLightSensor = mSensorManager.getDefaultSensor(TYPE_LIGHT);
            mSensorManager.registerListener(this, mLightSensor, SENSOR_DELAY_NORMAL);
        }
    }

    /**
     * Checks if the device has a light sensor.
     *
     * @return <code>true</code> if the device has a relative humidity internal sensor. <code>false</code> otherwise.
     */
    public boolean hasLightSensor() {
        return mSensorManager.getSensorList(Sensor.TYPE_LIGHT).size() > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSensorChanged(@NonNull final SensorEvent event) {
        if (mLightSensor == null) {
            Log.e(TAG, "onSensorChanged -> Sensor %s is not initialized yet.");
            return;
        }
        final float lux = event.values[0];
        Log.d(TAG, String.format("onSensorChanged -> Light sensor with name %s retrieved: %f Lux.", mLightSensor.getName(), lux));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAccuracyChanged(@NonNull final Sensor sensor, final int accuracy) {
        Log.i(TAG, String.format("onAccuracyChanged -> Accuracy from sensor %s changed to %d.", sensor.getName(), accuracy));
    }
}