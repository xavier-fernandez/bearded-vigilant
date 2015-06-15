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

package com.bearded.modules.ble.discovery.persistence;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bearded.modules.ble.discovery.domain.BleDeviceEntity;
import com.bearded.modules.ble.discovery.domain.BleEventEntity;
import com.bearded.modules.ble.discovery.domain.BleEventSeriesEntity;
import com.bearded.modules.ble.discovery.persistence.dao.DaoSession;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sensirion.libble.devices.BleDevice;
import com.sensirion.libble.devices.DeviceBluetoothType;

import java.util.List;

public class BleDiscoveryDatabaseFacade {

    @NonNull
    private static final String TAG = BleDiscoveryDatabaseFacade.class.getSimpleName();
    @NonNull
    private static final String DATABASE_NAME_SUFFIX = "bluetooth-discovery-db";
    @NonNull
    private final BleDeviceEntityFacade mSensorEntityFacade;
    @NonNull
    private final BleEventSeriesEntityFacade mBleEventSeriesEntityFacade;
    @NonNull
    private final BleEventEntityFacade mBleEventEntityFacade;
    @NonNull
    private final DatabaseConnector mDatabaseHandler;

    public BleDiscoveryDatabaseFacade(@NonNull final Context context,
                                      final int binSizeMilliseconds) {
        final String databaseName = String.format("%s-%s", TAG, DATABASE_NAME_SUFFIX);
        mDatabaseHandler = new DatabaseConnector(context, databaseName);
        mSensorEntityFacade = new BleDeviceEntityFacade();
        mBleEventSeriesEntityFacade = new BleEventSeriesEntityFacade();
        mBleEventEntityFacade = new BleEventEntityFacade(binSizeMilliseconds);
    }

    /**
     * Inserts a BleEvent inside the database.
     *
     * @param device of the BleEvent.
     */
    public void insertBleEvent(@NonNull final BleDevice device) {
        synchronized (mDatabaseHandler) {
            final String deviceAddress = device.getAddress();
            if (deviceAddress == null){
                return;
            }
            Boolean classic = null;
            Boolean ble = null;
            if (device.getBluetoothType() == DeviceBluetoothType.DEVICE_TYPE_CLASSIC){
                classic = true;
                ble = false;
            } else if (device.getBluetoothType() == DeviceBluetoothType.DEVICE_TYPE_DUAL){
                classic = true;
                ble = true;
            } else if (device.getBluetoothType() == DeviceBluetoothType.DEVICE_TYPE_LE){
                classic = false;
                ble = false;
            }
            final DaoSession session = mDatabaseHandler.getSession();
            final BleDeviceEntity deviceEntity = mSensorEntityFacade.getBleDeviceEntity(session, device.getAddress(), device.getAdvertisedName(), classic, ble);
            final BleEventSeriesEntity activeEventSeries = mBleEventSeriesEntityFacade.getActiveEventSeries(session, deviceEntity);
            mBleEventEntityFacade.addMeasurement(session, activeEventSeries, (byte) device.getRSSI());
        }
    }

    /**
     * Converts the stored data into JSON, for sending the data to the cloud.
     *
     * @return {@link String} with the serialized data JSON file - <code>null</code> if there is no data.
     */
    @Nullable
    public JsonObject getBleDiscoveryDataJson(@NonNull final JsonObject metadata) {
        synchronized (mDatabaseHandler) {
            final DaoSession session = mDatabaseHandler.getSession();
            final JsonObject databaseJsonObject = new JsonObject();
            databaseJsonObject.add("deviceMetadata", metadata);
            mBleEventSeriesEntityFacade.updateAllEventSeriesEndTimestamp(session);
            final JsonArray sensorArray = new JsonArray();
            final List<BleDeviceEntity> sensors = mSensorEntityFacade.getAllBleDevices(session);
            Log.d(TAG, String.format("prepareDataForCloudUpload -> Preparing %d sensors.", sensors.size()));
            for (final BleDeviceEntity sensor : sensors) {
                final JsonObject sensorJsonObject = prepareBleDeviceJson(session, sensor);
                if (sensorJsonObject != null) {
                    sensorArray.add(sensorJsonObject);
                }
            }
            if (sensorArray.size() == 0) {
                Log.w(TAG, "prepareDataForCloudUpload -> No data for cloud upload.");
                return null;
            }

            Log.i(TAG, "prepareDataForCloudUpload -> " + sensorArray.toString());
            databaseJsonObject.add("eventData", sensorArray);
            return databaseJsonObject;
        }
    }

    @Nullable
    private JsonObject prepareBleDeviceJson(@NonNull final DaoSession session,
                                            @NonNull final BleDeviceEntity sensor) {
        Log.d(TAG, String.format("prepareDataForCloudUpload -> Preparing sensor %s with name: %s.", sensor.getId(), sensor.getDeviceAddress()));
        final JsonObject sensorJsonObject = sensor.toJsonObject();
        final JsonArray sensorSeriesJsonArray = new JsonArray();
        final List<BleEventSeriesEntity> closedSeries =
                mBleEventSeriesEntityFacade.getAllClosedEventSeriesFromDevice(session, sensor);
        if (closedSeries.isEmpty()) {
            Log.w(TAG, String.format("prepareBleDeviceJson -> Sensor with name %s do not have any measurement series.", sensor.getDeviceAddress()));
            return null;
        }
        for (final BleEventSeriesEntity series : closedSeries) {
            final JsonObject seriesJsonObject = prepareSeriesJson(session, series);
            if (seriesJsonObject != null) {
                sensorSeriesJsonArray.add(seriesJsonObject);
            }
        }
        sensorJsonObject.add("eventSeries", sensorSeriesJsonArray);
        return sensorJsonObject;
    }

    @Nullable
    private JsonObject prepareSeriesJson(@NonNull final DaoSession session,
                                         @NonNull final BleEventSeriesEntity series) {
        Log.d(TAG, String.format("prepareSeriesJson -> Preparing series id with name: %s.", series.getId()));
        final JsonObject seriesJsonObject = series.toJsonObject();
        final JsonArray seriesMeasurementsArray = new JsonArray();
        final List<BleEventEntity> measurements = mBleEventEntityFacade.getAllEventsFromSeries(session, series);
        if (measurements == null || measurements.isEmpty()) {
            Log.w(TAG, String.format("prepareSeriesJson -> Series with id %d do not have any events.", series.getId()));
            return null;
        }
        for (final BleEventEntity measurementEntity : measurements) {
            final JsonObject measurementJsonObject = measurementEntity.toJsonObject();
            Log.d(TAG, String.format("prepareSeriesJson -> Preparing events: %s.", measurementJsonObject.toString()));
            seriesMeasurementsArray.add(measurementJsonObject);
        }
        seriesJsonObject.add("events", seriesMeasurementsArray);
        return seriesJsonObject;
    }


    /**
     * Removes all the uploaded measurements from the internal database.
     */
    public void purgeAllUploadedBleEvents() {
        synchronized (mDatabaseHandler) {
            final DaoSession session = mDatabaseHandler.getSession();
            session.runInTx(new Runnable() {
                @Override
                public void run() {
                    for (final BleDeviceEntity sensor : mSensorEntityFacade.getAllBleDevices(session)) {
                        for (final BleEventSeriesEntity series : mBleEventSeriesEntityFacade.getAllClosedEventSeriesFromDevice(session, sensor)) {
                            final List<BleEventEntity> events = mBleEventEntityFacade.getAllEventsFromSeries(session, series);
                            if (events != null) {
                                for (final BleEventEntity measurement : events) {
                                    session.delete(measurement);
                                }
                            }
                            session.delete(series);
                        }
                    }
                }
            });
        }
    }
}