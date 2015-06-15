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
import android.hardware.Sensor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bearded.common.modules.AbstractCloudModule;
import com.bearded.common.sensor.SensorType;
import com.bearded.modules.sensor.persistence.SensorDatabaseFacade;
import com.bearded.modules.sensor.persistence.cloud.SensorCloudUploader;
import com.google.gson.JsonObject;
import com.sensirion.libble.BleManager;
import com.sensirion.libble.listeners.devices.DeviceStateListener;
import com.sensirion.libble.listeners.devices.ScanListener;

import org.joda.time.DateTime;

import static com.bearded.common.time.TimeUtils.millisecondsFromNow;

/**
 * This abstract class should be inherited by all the internal sensor modules which wants
 * to listen for notifications from the default sensor for the given {@link SensorType}
 */
abstract class AbstractBleSensorModule extends AbstractCloudModule implements DeviceStateListener, ScanListener {

    @NonNull
    private final String TAG = getClass().getSimpleName();

    @NonNull
    private final SensorType mSensorType;
    @Nullable
    private final SensorDatabaseFacade mDatabaseFacade;
    @NonNull
    private final SensorCloudUploader mInternalSensorCloudUploader;

    private byte mConsecutiveTimeouts = 0;

    protected AbstractBleSensorModule(@NonNull final Context context,
                                      @NonNull final SensorType sensorType,
                                      final int binSizeMillis) {
        super(context);
        mSensorType = sensorType;
        mDatabaseFacade = new SensorDatabaseFacade(context, sensorType, binSizeMillis);
        mInternalSensorCloudUploader = new SensorCloudUploader();
        BleManager.getInstance().init(context.getApplicationContext());
        BleManager.getInstance().registerNotificationListener(this);
        BleManager.getInstance().startScanning();
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
     * Obtains the {@link SensorDatabaseFacade} of the module {@link Sensor}
     *
     * @return the module {@link SensorDatabaseFacade}
     */
    @Nullable
    protected SensorDatabaseFacade getDatabaseFacade() {
        return mDatabaseFacade;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean isModuleEnabled() {
        final DateTime lastReceivedDataTime = getLastSensorDataReceived();
        if (lastReceivedDataTime == null) {
            return false;
        }
        return millisecondsFromNow(lastReceivedDataTime) > DEFAULT_SENSOR_TIMEOUT_MILLISECONDS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pushCloudDataToTheCloud() {
        if (mDatabaseFacade == null) {
            Log.w(TAG, "pushCloudDataToTheCloud -> Database facade is null.");
        } else {
            final JsonObject sensorDataJson = mDatabaseFacade.getSensorDataJson(getDeviceMetadataJson());
            if (sensorDataJson == null) {
                Log.w(TAG, "pushCloudDataToTheCloud -> No data to upload.");
            } else {
                mInternalSensorCloudUploader.uploadSensorData(sensorDataJson.toString(), this);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUploadCompleted(final int code) {
        super.onUploadCompleted(code);
        Log.d(TAG, String.format("onUploadCompleted with code: %d", code));
        assert mDatabaseFacade != null;
        mDatabaseFacade.removeAllUploadedSensorMeasurements();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUploadFailure(@Nullable final String message) {
        Log.d(TAG, String.format("onUploadFailure with message: %s", message));
        if (message != null && message.startsWith("timeout")) {
            assert mDatabaseFacade != null;
            synchronized (this) {
                if (mConsecutiveTimeouts++ > 3) {
                    mConsecutiveTimeouts = 0;
                    Log.w(TAG, "onUploadFailure -> Purging database, file is to big for sending it completely to the cloud.");
                    mDatabaseFacade.removeAllUploadedSensorMeasurements();
                }
            }
        }
    }
}