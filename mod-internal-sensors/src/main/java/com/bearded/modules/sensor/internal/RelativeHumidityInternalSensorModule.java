package com.bearded.modules.sensor.internal;

import android.content.Context;
import android.hardware.SensorEvent;
import android.util.Log;

import com.bearded.common.sensor.SensorType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

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
public class RelativeHumidityInternalSensorModule extends AbstractInternalSensorManager {

    private static final String TAG = RelativeHumidityInternalSensorModule.class.getSimpleName();

    private static final int RELATIVE_HUMIDITY_INTERNAL_SENSOR_MODULE_VERSION = 1;

    @Nullable
    private DateTime mLastSensorValueReceivedTime;

    public RelativeHumidityInternalSensorModule(@NotNull final Context context) {
        super(context, SensorType.RELATIVE_HUMIDITY);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String getModuleName() {
        return TAG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getModuleVersion() {
        return RELATIVE_HUMIDITY_INTERNAL_SENSOR_MODULE_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public DateTime getLastSensorDataReceived() {
        return mLastSensorValueReceivedTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSensorChanged(@NotNull final SensorEvent event) {
        if (mInternalSensor == null) {
            Log.e(TAG, "onSensorChanged -> Sensor %s is not initialized yet.");
            return;
        }
        final float rh = event.values[0];
        mLastSensorValueReceivedTime = DateTime.now();
        Log.d(TAG, String.format("onSensorChanged -> Sensor with name %s retrieved: %f%% of relative humidity.", mInternalSensor.getName(), rh));
    }
}
