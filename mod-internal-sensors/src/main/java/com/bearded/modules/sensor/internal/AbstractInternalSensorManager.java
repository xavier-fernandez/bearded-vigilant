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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bearded.common.modules.Module;
import com.bearded.common.sensor.SensorType;
import com.bearded.modules.sensor.internal.persistence.InternalSensorDatabaseFacade;

import org.joda.time.DateTime;

import static android.content.Context.SENSOR_SERVICE;
import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;
import static com.bearded.common.time.TimeUtils.millisecondsFromNow;

/**
 * This abstract class should be inherited by all the internal sensor modules which wants
 * to listen for notifications from the default sensor for the given {@link SensorType}
 */
abstract class AbstractInternalSensorManager implements Module, SensorEventListener {

    private static final String TAG = AbstractInternalSensorManager.class.getSimpleName();

    @NonNull
    private final SensorManager mSensorManager;
    @NonNull
    private final SensorType mSensorType;
    @Nullable
    private final Sensor mInternalSensor;
    @Nullable
    private final InternalSensorDatabaseFacade mDatabaseFacade;

    protected AbstractInternalSensorManager(@NonNull final Context context,
                                            @NonNull final SensorType sensorType,
                                            final int binSizeMillis) {
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        mSensorType = sensorType;
        mInternalSensor = getSensorManager().getDefaultSensor(sensorType.getSensorId());
        if (getSensor() == null) {
            final String typeName = sensorType.getSensorTypeName();
            Log.w(TAG, String.format("%s -> The device do not have a %s sensor.", TAG, typeName));
            mDatabaseFacade = null;
        } else {
            getSensorManager().registerListener(this, getSensor(), SENSOR_DELAY_NORMAL);
            mDatabaseFacade = new InternalSensorDatabaseFacade(context, sensorType, binSizeMillis);
        }
    }

    /**
     * Returns the sensor type of the module.
     *
     * @return {@link com.bearded.common.sensor.SensorType}
     */
    @NonNull
    public SensorType getSensorType() {
        return mSensorType;
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
     * Obtains the {@link InternalSensorDatabaseFacade} of the module {@link Sensor}
     *
     * @return the module {@link InternalSensorDatabaseFacade}
     */
    @Nullable
    protected InternalSensorDatabaseFacade getDatabaseFacade() {
        return mDatabaseFacade;
    }

    /**
     * Obtains the {@link SensorManager} of the module {@link Sensor}
     *
     * @return the module {@link SensorManager}
     */
    @NonNull
    protected SensorManager getSensorManager() {
        return mSensorManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean isModuleEnabled() {
        if (getSensor() == null) {
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
    public void onAccuracyChanged(@NonNull final Sensor sensor, final int accuracy) {
        Log.i(TAG, String.format("onAccuracyChanged -> Accuracy changed to %d.", accuracy));
    }
}