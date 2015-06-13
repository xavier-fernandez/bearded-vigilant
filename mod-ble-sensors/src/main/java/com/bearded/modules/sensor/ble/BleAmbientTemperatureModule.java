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
package com.bearded.modules.sensor.ble;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bearded.common.sensor.SensorType;
import com.sensirion.libble.devices.BleDevice;
import com.sensirion.libble.listeners.services.TemperatureListener;
import com.sensirion.libble.utils.TemperatureUnit;

import org.joda.time.DateTime;

/**
 * Class instantiation is done with refraction in {@see com.bearded.vigilant.ModuleManager}
 */
@SuppressWarnings("unused")
public class BleAmbientTemperatureModule extends AbstractBleSensorModule implements TemperatureListener {

    private static final String TAG = BleAmbientTemperatureModule.class.getSimpleName();

    private static final int DATA_BIN_TIME_MS = 4 * 1000; // 4 SECONDS

    private static final int BLE_AMBIENT_TEMPERATURE_MODULE_VERSION_NUMBER = 1;

    @Nullable
    private DateTime mLastSensorValueReceivedTime;

    public BleAmbientTemperatureModule(@NonNull final Context context) {
        super(context, SensorType.AMBIENT_TEMPERATURE, DATA_BIN_TIME_MS);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public String getModuleName() {
        return TAG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getModuleVersion() {
        return BLE_AMBIENT_TEMPERATURE_MODULE_VERSION_NUMBER;
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
    public void onNewTemperature(@NonNull final BleDevice device,
                                 final float temperature,
                                 @NonNull final String sensorName,
                                 @NonNull final TemperatureUnit unit) {

    }

    @Override
    public void onNewHistoricalTemperature(@NonNull final BleDevice device,
                                           final float temperature,
                                           final long timestampMillis,
                                           @NonNull final String sensorName,
                                           @NonNull final TemperatureUnit unit) {
        // TODO: Implement
    }
}
