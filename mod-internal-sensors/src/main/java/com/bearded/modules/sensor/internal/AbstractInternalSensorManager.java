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

package com.bearded.modules.sensor.internal;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.bearded.common.modules.Module;
import com.bearded.common.sensor.SensorType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import static android.content.Context.SENSOR_SERVICE;
import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;
import static com.bearded.common.utils.TimeUtils.millisecondsFromNow;

/**
 * This abstract class should be inherited by all the internal sensor modules which wants
 * to listen for notifications from the default sensor for the given {@link SensorType}
 */
abstract class AbstractInternalSensorManager implements Module, SensorEventListener {

    private static final String TAG = AbstractInternalSensorManager.class.getSimpleName();

    @NotNull
    protected final SensorManager mSensorManager;
    @NotNull
    protected final SensorType mSensorType;
    @Nullable
    protected final Sensor mInternalSensor;

    protected AbstractInternalSensorManager(@NotNull final Context context,
                                            @NotNull final SensorType sensorType) {
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        mSensorType = sensorType;
        mInternalSensor = mSensorManager.getDefaultSensor(sensorType.getSensorId());
        if (mInternalSensor == null){
            final String typeName = sensorType.getSensorTypeName();
            Log.w(TAG, String.format("%s -> The device do not have a %s sensor.", TAG, typeName));
        } else {
             mSensorManager.registerListener(this, mInternalSensor, SENSOR_DELAY_NORMAL);
        }
    }

    /**
     * Returns the default sensor of the module {@link SensorType}
     *
     * @return {@link Sensor} used in the module - <code>null</code> if is not available.
     */
    @Nullable
    public Sensor getSensor() {
        return mInternalSensor;
    }

    /**
     * Returns the sensor type of the module.
     *
     * @return {@link com.bearded.common.sensor.SensorType}
     */
    @NotNull
    public SensorType getSensorType() {
        return mSensorType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean isModuleEnabled() {
        if (mInternalSensor == null) {
            return false;
        }
        final DateTime lastReceivedDataTime = getLastSensorDataReceived();
        if (lastReceivedDataTime == null) {
            return false;
        }
        return millisecondsFromNow(lastReceivedDataTime) > DEFAULT_SENSOR_TIMEOUT_MILLISECONDS;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public DateTime getLastCloudUploadTime() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAccuracyChanged(@NotNull final Sensor sensor, final int accuracy) {
        Log.i(TAG, String.format("onAccuracyChanged -> Accuracy changed to %d.", accuracy));
    }
}
