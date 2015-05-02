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
        mMeasurementEntityFacade = new InternalSensorMeasurementEntityFacade(sensorType, binSizeMilliseconds);
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
     * @return {@link String} with the serialized data JSON file.
     */
    @NonNull
    public JsonObject prepareDataForCloudUpload() {
        synchronized (mDatabaseHandler) {
            final DaoSession session = mDatabaseHandler.getSession();
            mMeasurementSeriesEntityFacade.updateAllMeasurementSeriesEndTimestamp(session);
            final JsonArray sensorArray = new JsonArray();
            final List<InternalSensorEntity> sensors = mSensorEntityFacade.getAllSensorEntities(session);
            Log.d(TAG, String.format("prepareDataForCloudUpload -> Preparing %d sensors.", sensors.size()));
            for (final InternalSensorEntity sensor : sensors) {
                Log.d(TAG, String.format("prepareDataForCloudUpload -> Preparing sensor %s with name: %s.", sensor.getId(), sensor.getSensorName()));
                final JsonObject sensorJsonObject = sensor.toJsonObject();
                final JsonArray sensorSeriesJsonArray = new JsonArray();
                for (final InternalSensorMeasurementSeriesEntity series : mMeasurementSeriesEntityFacade.getAllClosedMeasurementSeriesFromSensor(session, sensor)) {
                    Log.d(TAG, String.format("prepareDataForCloudUpload -> Preparing series id with name: %s.", series.getId()));
                    final JsonObject seriesJsonObject = series.toJsonObject();
                    final JsonArray seriesMeasurementsArray = new JsonArray();
                    for (final InternalSensorMeasurementEntity measurementEntity : mMeasurementEntityFacade.obtainAllMeasurementsFromSeries(session, series)) {
                        final JsonObject measurementJsonObject = measurementEntity.toJsonObject();
                        Log.d(TAG, String.format("prepareDataForCloudUpload -> Preparing measurement: %s.", measurementJsonObject.toString()));
                        seriesMeasurementsArray.add(measurementJsonObject);
                    }
                    seriesJsonObject.add("measurements", seriesMeasurementsArray);
                    sensorSeriesJsonArray.add(seriesJsonObject);
                }
                sensorJsonObject.add("measurementSeries", sensorSeriesJsonArray);
                sensorArray.add(sensorJsonObject);
            }
            Log.i(TAG, "prepareDataForCloudUpload -> " + sensorArray.toString());
            final JsonObject databaseJsonObject = new JsonObject();
            databaseJsonObject.add("sensors", sensorArray);
            return databaseJsonObject;
        }
    }
}