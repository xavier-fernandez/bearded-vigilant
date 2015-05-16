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
package com.bearded.modules.sensor.internal.persistence;

import android.content.Context;
import android.hardware.Sensor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bearded.common.sensor.SensorType;
import com.bearded.modules.sensor.internal.domain.InternalSensorEntity;
import com.bearded.modules.sensor.internal.domain.InternalSensorMeasurementEntity;
import com.bearded.modules.sensor.internal.domain.InternalSensorMeasurementSeriesEntity;
import com.bearded.modules.sensor.internal.persistence.dao.DaoSession;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

public class InternalSensorDatabaseFacade {

    private static final String TAG = InternalSensorDatabaseFacade.class.getSimpleName();

    private static final String DATABASE_NAME_SUFFIX = "internal-sensor-db";

    @NonNull
    private final InternalSensorEntityFacade mSensorEntityFacade;
    @NonNull
    private final InternalSensorMeasurementSeriesEntityFacade mMeasurementSeriesEntityFacade;
    @NonNull
    private final InternalSensorMeasurementEntityFacade mMeasurementEntityFacade;

    @NonNull
    private final DatabaseConnector mDatabaseHandler;

    public InternalSensorDatabaseFacade(@NonNull final Context context,
                                        @NonNull final SensorType sensorType,
                                        final int binSizeMilliseconds) {
        final String databaseName = String.format("%s-%s", sensorType.getSensorTypeName(), DATABASE_NAME_SUFFIX);
        mDatabaseHandler = new DatabaseConnector(context, databaseName);
        mSensorEntityFacade = new InternalSensorEntityFacade();
        mMeasurementSeriesEntityFacade = new InternalSensorMeasurementSeriesEntityFacade();
        mMeasurementEntityFacade = new InternalSensorMeasurementEntityFacade(binSizeMilliseconds);
    }

    /**
     * Writes in the database a sensor reading.
     */
    public void insertReadingDatabase(@NonNull final Sensor sensor,
                                      final float measurement) {
        synchronized (mDatabaseHandler) {
            final DaoSession session = mDatabaseHandler.getSession();
            session.runInTx(new Runnable() {
                @Override
                public void run() {
                    final InternalSensorEntity sensorEntity = mSensorEntityFacade.getSensorEntity(session, sensor);
                    final InternalSensorMeasurementSeriesEntity series = mMeasurementSeriesEntityFacade.getActiveMeasurementSeries(session, sensorEntity);
                    mMeasurementEntityFacade.addMeasurement(session, series, measurement);
                }
            });
        }
    }

    /**
     * Converts the stored data into JSON, for sending the data to the cloud.
     *
     * @return {@link String} with the serialized data JSON file - <code>null</code> if there is no data.
     */
    @Nullable
    public JsonObject prepareDataForCloudUpload() {
        synchronized (mDatabaseHandler) {
            final DaoSession session = mDatabaseHandler.getSession();
            mMeasurementSeriesEntityFacade.updateAllMeasurementSeriesEndTimestamp(session);
            final JsonArray sensorArray = new JsonArray();
            final List<InternalSensorEntity> sensors = mSensorEntityFacade.getAllSensorEntities(session);
            Log.d(TAG, String.format("prepareDataForCloudUpload -> Preparing %d sensors.", sensors.size()));
            for (final InternalSensorEntity sensor : sensors) {
                final JsonObject sensorJsonObject = prepareSensorJson(session, sensor);
                if (sensorJsonObject != null) {
                    sensorArray.add(sensorJsonObject);
                }
            }
            if (sensorArray.size() == 0) {
                Log.w(TAG, "prepareDataForCloudUpload -> No data for cloud upload.");
                return null;
            }

            Log.i(TAG, "prepareDataForCloudUpload -> " + sensorArray.toString());
            final JsonObject databaseJsonObject = new JsonObject();
            databaseJsonObject.add("sensors", sensorArray);
            return databaseJsonObject;
        }
    }

    @Nullable
    private JsonObject prepareSensorJson(@NonNull final DaoSession session,
                                         @NonNull final InternalSensorEntity sensor) {
        Log.d(TAG, String.format("prepareDataForCloudUpload -> Preparing sensor %s with name: %s.", sensor.getId(), sensor.getSensorName()));
        final JsonObject sensorJsonObject = sensor.toJsonObject();
        final JsonArray sensorSeriesJsonArray = new JsonArray();
        final List<InternalSensorMeasurementSeriesEntity> closedSeries =
                mMeasurementSeriesEntityFacade.getAllClosedMeasurementSeriesFromSensor(session, sensor);
        if (closedSeries.isEmpty()) {
            Log.w(TAG, String.format("prepareSensorJson -> Sensor with name %s do not have any measurement series.", sensor.getSensorName()));
            return null;
        }
        for (final InternalSensorMeasurementSeriesEntity series : closedSeries) {
            final JsonObject seriesJsonObject = prepareSeriesJson(session, series);
            if (seriesJsonObject != null) {
                sensorSeriesJsonArray.add(seriesJsonObject);
            }
        }
        sensorJsonObject.add("measurementSeries", sensorSeriesJsonArray);
        return sensorJsonObject;
    }

    @Nullable
    private JsonObject prepareSeriesJson(@NonNull final DaoSession session,
                                         @NonNull final InternalSensorMeasurementSeriesEntity series) {
        Log.d(TAG, String.format("prepareSeriesJson -> Preparing series id with name: %s.", series.getId()));
        final JsonObject seriesJsonObject = series.toJsonObject();
        final JsonArray seriesMeasurementsArray = new JsonArray();
        final List<InternalSensorMeasurementEntity> measurements = mMeasurementEntityFacade.getAllMeasurementsFromSeries(session, series);
        if (measurements == null || measurements.isEmpty()) {
            Log.w(TAG, String.format("prepareSeriesJson -> Series with id %d do not have any measurement.", series.getId()));
            return null;
        }
        for (final InternalSensorMeasurementEntity measurementEntity : measurements) {
            final JsonObject measurementJsonObject = measurementEntity.toJsonObject();
            Log.d(TAG, String.format("prepareSeriesJson -> Preparing measurement: %s.", measurementJsonObject.toString()));
            seriesMeasurementsArray.add(measurementJsonObject);
        }
        seriesJsonObject.add("measurements", seriesMeasurementsArray);
        return seriesJsonObject;
    }
}