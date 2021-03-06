package com.bearded.modules.sensor.persistence;

import android.content.Context;
import android.hardware.Sensor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bearded.common.sensor.SensorType;
import com.bearded.modules.sensor.domain.SensorEntity;
import com.bearded.modules.sensor.domain.SensorMeasurementEntity;
import com.bearded.modules.sensor.domain.SensorMeasurementSeriesEntity;
import com.bearded.modules.sensor.persistence.dao.DaoSession;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

public class SensorDatabaseFacade {

    @NonNull
    private static final String TAG = SensorDatabaseFacade.class.getSimpleName();

    @NonNull
    private static final String DATABASE_NAME_SUFFIX = "sensor-db";

    @NonNull
    private final SensorEntityFacade mSensorEntityFacade;
    @NonNull
    private final SensorMeasurementSeriesEntityFacade mMeasurementSeriesEntityFacade;
    @NonNull
    private final SensorMeasurementEntityFacade mMeasurementEntityFacade;
    @NonNull
    private final DatabaseConnector mDatabaseHandler;

    public SensorDatabaseFacade(@NonNull Context context,
                                @NonNull SensorType sensorType,
                                int binSizeMilliseconds) {
        final String databaseName = String.format("%s-%s", sensorType.getSensorTypeName(), DATABASE_NAME_SUFFIX);
        mDatabaseHandler = new DatabaseConnector(context, databaseName);
        mSensorEntityFacade = new SensorEntityFacade();
        mMeasurementSeriesEntityFacade = new SensorMeasurementSeriesEntityFacade();
        mMeasurementEntityFacade = new SensorMeasurementEntityFacade(binSizeMilliseconds);
    }

    /**
     * Inserts into the database a sensor reading.
     */
    public void insertSensorReading(@NonNull final Sensor sensor, final float measurement) {
        final SensorEntity sensorEntity;
        synchronized (mDatabaseHandler) {
            final DaoSession session = mDatabaseHandler.getSession();
            sensorEntity = mSensorEntityFacade.getSensorEntity(session, sensor);
        }
        storeReading(sensorEntity, measurement);
    }

    /**
     * Inserts into the database a sensor reading.
     */
    public void insertSensorReading(float measurement,
                                    @NonNull String sensorAddress,
                                    @NonNull SensorType sensorType,
                                    @NonNull String sensorUnit,
                                    @NonNull String sensorName) {
        final SensorEntity sensorEntity;
        synchronized (mDatabaseHandler) {
            final DaoSession session = mDatabaseHandler.getSession();
            sensorEntity = mSensorEntityFacade.getSensorEntity(session, sensorAddress, sensorType, sensorUnit, sensorName);
        }
        storeReading(sensorEntity, measurement);
    }

    /**
     * Inserts into the database a sensor reading.
     */
    private void storeReading(@NonNull final SensorEntity sensorEntity, final float measurement) {
        synchronized (mDatabaseHandler) {
            final DaoSession session = mDatabaseHandler.getSession();
            session.runInTx(new Runnable() {
                @Override
                public void run() {
                    session.insertOrReplace(sensorEntity);
                    final SensorMeasurementSeriesEntity series = mMeasurementSeriesEntityFacade.getActiveMeasurementSeries(session, sensorEntity);
                    mMeasurementEntityFacade.addMeasurement(session, series, measurement);
                }
            });
        }
    }

    /**
     * Removes all the uploaded measurements from the internal database.
     */
    public void removeAllUploadedSensorMeasurements() {
        synchronized (mDatabaseHandler) {
            final DaoSession session = mDatabaseHandler.getSession();
            session.runInTx(new Runnable() {
                @Override
                public void run() {
                    for (final SensorEntity sensor : mSensorEntityFacade.getAllSensorEntities(session)) {
                        for (final SensorMeasurementSeriesEntity series : mMeasurementSeriesEntityFacade.getAllClosedMeasurementSeriesFromSensor(session, sensor)) {
                            for (final SensorMeasurementEntity measurement : mMeasurementEntityFacade.getAllMeasurementsFromSeries(session, series)) {
                                session.delete(measurement);
                            }
                            session.delete(series);
                        }
                    }
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
    public JsonObject getSensorDataJson(@NonNull JsonObject metadata) {
        synchronized (mDatabaseHandler) {
            final DaoSession session = mDatabaseHandler.getSession();
            final JsonObject databaseJsonObject = new JsonObject();
            databaseJsonObject.add("deviceMetadata", metadata);
            mMeasurementSeriesEntityFacade.updateAllMeasurementSeriesEndTimestamp(session);
            final JsonArray sensorArray = new JsonArray();
            final List<SensorEntity> sensors = mSensorEntityFacade.getAllSensorEntities(session);
            Log.d(TAG, String.format("prepareDataForCloudUpload -> Preparing %d sensors.", sensors.size()));
            for (final SensorEntity sensor : sensors) {
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
            databaseJsonObject.add("sensorData", sensorArray);
            return databaseJsonObject;
        }
    }

    @Nullable
    private JsonObject prepareSensorJson(@NonNull DaoSession session,
                                         @NonNull SensorEntity sensor) {
        Log.d(TAG, String.format("prepareDataForCloudUpload -> Preparing sensor %s with name: %s.", sensor.getId(), sensor.getSensorName()));
        final JsonObject sensorJsonObject = sensor.toJsonObject();
        final JsonArray sensorSeriesJsonArray = new JsonArray();
        final List<SensorMeasurementSeriesEntity> closedSeries =
                mMeasurementSeriesEntityFacade.getAllClosedMeasurementSeriesFromSensor(session, sensor);
        if (closedSeries.isEmpty()) {
            Log.w(TAG, String.format("prepareSensorJson -> Sensor with name %s do not have any measurement series.", sensor.getSensorName()));
            return null;
        }
        for (final SensorMeasurementSeriesEntity series : closedSeries) {
            final JsonObject seriesJsonObject = prepareSeriesJson(session, series);
            if (seriesJsonObject != null) {
                sensorSeriesJsonArray.add(seriesJsonObject);
            }
        }
        sensorJsonObject.add("measurementSeries", sensorSeriesJsonArray);
        return sensorJsonObject;
    }

    @Nullable
    private JsonObject prepareSeriesJson(@NonNull DaoSession session,
                                         @NonNull SensorMeasurementSeriesEntity series) {
        Log.d(TAG, String.format("prepareSeriesJson -> Preparing series id with name: %s.", series.getId()));
        final JsonObject seriesJsonObject = series.toJsonObject();
        final JsonArray seriesMeasurementsArray = new JsonArray();
        final List<SensorMeasurementEntity> measurements = mMeasurementEntityFacade.getAllMeasurementsFromSeries(session, series);
        if (measurements == null || measurements.isEmpty()) {
            Log.w(TAG, String.format("prepareSeriesJson -> Series with id %d do not have any measurement.", series.getId()));
            return null;
        }
        for (final SensorMeasurementEntity measurementEntity : measurements) {
            final JsonObject measurementJsonObject = measurementEntity.toJsonObject();
            Log.d(TAG, String.format("prepareSeriesJson -> Preparing measurement: %s.", measurementJsonObject.toString()));
            seriesMeasurementsArray.add(measurementJsonObject);
        }
        seriesJsonObject.add("measurements", seriesMeasurementsArray);
        return seriesJsonObject;
    }
}