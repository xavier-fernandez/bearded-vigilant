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
import android.util.Log;

import com.bearded.common.sensor.SensorType;
import com.bearded.modules.sensor.domain.SensorEntity;
import com.sensirion.libble.BleManager;
import com.sensirion.libble.devices.BleDevice;
import com.sensirion.libble.devices.KnownDevices;
import com.sensirion.libble.listeners.services.HumidityListener;
import com.sensirion.libble.utils.HumidityUnit;

import org.joda.time.DateTime;

/**
 * Class instantiation is done with refraction in {@see com.bearded.vigilant.ModuleManager}
 */
@SuppressWarnings("unused")
public class BleRelativeHumidityModule extends AbstractBleSensorModule implements HumidityListener {

    @NonNull
    private static final String TAG = BleRelativeHumidityModule.class.getSimpleName();

    private static final int DATA_BIN_TIME_MS = 4 * 1000; // 4 SECONDS

    private static final int BLE_HUMIDITY_MODULE_VERSION_NUMBER = 1;
    private static final long ONE_MINUTE_MS = 60l * 1000l;
    private static long mLastDeviceConnectionPetitionTimestamp = 0;
    @NonNull
    private final Context mApplicationContext;

    @Nullable
    private DateTime mLastSensorValueReceivedTime;

    public BleRelativeHumidityModule(@NonNull final Context context) {
        super(context, SensorType.RELATIVE_HUMIDITY, DATA_BIN_TIME_MS);
        mApplicationContext = context.getApplicationContext();
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
        return BLE_HUMIDITY_MODULE_VERSION_NUMBER;
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
    public void onNewHumidity(@NonNull final BleDevice device,
                              final float humidity,
                              @NonNull final String sensorName,
                              @NonNull final HumidityUnit unit) {
        if (getDatabaseFacade() != null) {
            Log.d(TAG, String.format("onNewHumidity -> Received humidity %f from sensor %s from the device %s.", humidity, sensorName, device.getAddress()));
            final SensorEntity bleSensor = new SensorEntity();
            bleSensor.setSensorAddress(device.getAddress());
            final String sensorAddress = device.getAddress();
            final String sensorUnit = unit.name();
            getDatabaseFacade().insertSensorReading(humidity, sensorAddress, SensorType.RELATIVE_HUMIDITY, sensorUnit, sensorName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNewHistoricalHumidity(@NonNull final BleDevice device,
                                        final float relativeHumidity,
                                        final long timestampMilliseconds,
                                        @NonNull final String sensorName,
                                        @NonNull final HumidityUnit unit) {
        // TODO: Implement
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDeviceConnected(@NonNull final BleDevice device) {
        device.registerDeviceListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDeviceDisconnected(@NonNull final BleDevice device) {
        // TODO: Implement
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDeviceDiscovered(@NonNull final BleDevice device) {
        if (KnownDevices.TEMPERATURE_GADGETS.getAdvertisedNames().contains(device.getAdvertisedName())) {
            if (!device.isConnected()) {
                synchronized (this) {
                    if (System.currentTimeMillis() - ONE_MINUTE_MS > mLastDeviceConnectionPetitionTimestamp) {
                        mLastDeviceConnectionPetitionTimestamp = System.currentTimeMillis();
                        Log.d(TAG, String.format("onDeviceDiscovered -> Connecting to device %s with address %s.", device.getAdvertisedName(), device.getAddress()));
                        device.connect(mApplicationContext);
                        device.setAllNotificationsEnabled(true);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDeviceAllServicesDiscovered(@NonNull final BleDevice device) {
        Log.d(TAG, String.format("onDeviceAllServicesDiscovered -> Device %s has all its services available.  ", device.getAddress()));
        device.registerDeviceListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScanStateChanged(final boolean isScanEnabled) {
        if (!isScanEnabled) {
            BleManager.getInstance().startScanning();
        }
    }
}
