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

import android.support.annotation.NonNull;
import android.util.Log;

import com.bearded.common.time.TimeUtils;
import com.bearded.modules.sensor.internal.domain.SensorMeasurementEntity;
import com.bearded.modules.sensor.internal.domain.SensorMeasurementSeriesEntity;
import com.bearded.modules.sensor.internal.persistence.dao.DaoSession;
import com.bearded.modules.sensor.internal.persistence.dao.SensorMeasurementEntityDao;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;

class SensorMeasurementEntityFacade {

    @NonNull
    private static final String TAG = SensorMeasurementEntityFacade.class.getSimpleName();
    @NonNull
    private final Map<SensorMeasurementSeriesEntity, SensorMeasurementsBuffer> mSensorMeasurements;
    @NonNull
    private final Integer mTimeoutMillis;

    public SensorMeasurementEntityFacade(final int timeoutMillis) {
        mSensorMeasurements = Collections.synchronizedMap(
                new HashMap<SensorMeasurementSeriesEntity, SensorMeasurementsBuffer>());
        if (timeoutMillis <= 0) {
            throw new IllegalArgumentException(
                    String.format("%s: Constructor -> TimeoutMillis needs to be a positive number.", TAG));
        }
        mTimeoutMillis = timeoutMillis;
    }

    /**
     * Stores a measurement in the database.
     *
     * @param session     needed to store the measurement.
     * @param series      of the measurement.
     * @param measurement that will be stored.
     */
    public synchronized void addMeasurement(@NonNull final DaoSession session,
                                            @NonNull final SensorMeasurementSeriesEntity series,
                                            final float measurement) {
        SensorMeasurementsBuffer measurements = mSensorMeasurements.get(series);
        if (measurements == null) {
            //The first inserted element will be written inside the database.
            measurements = new SensorMeasurementsBuffer();
            measurements.addMeasurement(measurement);
            mSensorMeasurements.put(series, measurements);
            storeMeasurement(session, series, measurements);
        } else {
            measurements.addMeasurement(measurement);
            if (TimeUtils.millisecondsFromNow(measurements.firstElementTime) > mTimeoutMillis) {
                storeMeasurement(session, series, measurements);
            }
        }
    }

    private void storeMeasurement(@NonNull final DaoSession session,
                                  @NonNull final SensorMeasurementSeriesEntity series,
                                  @NonNull final SensorMeasurementsBuffer measurements) {
        final SensorMeasurementEntity measurementEntity = new SensorMeasurementEntity();
        measurementEntity.setStartTimestamp(TimeUtils.timestampToISOString(measurements.firstElementTime));
        measurementEntity.setEndTimestamp(TimeUtils.nowToISOString());
        measurementEntity.setBinSize(measurements.getBinSize());
        measurementEntity.setMedianSensorValue(measurements.getMidSensorValue());
        measurementEntity.setSensorMeasurementSeriesEntity(series);
        session.insert(measurementEntity);
        measurements.clear();
        Log.d(TAG, String.format("addMeasurement -> The following measurement was inserted in the database -> %s",
                measurementEntity.toJsonObject().toString()));
    }

    /**
     * Inserts all open measurements buffer inside the database.
     *
     * @param session needed to insert all open measurements inside the database.
     */
    void storeAllOpenMeasurements(@NonNull final DaoSession session) {
        for (final SensorMeasurementSeriesEntity seriesEntity : mSensorMeasurements.keySet()) {
            final SensorMeasurementsBuffer measurementsBuffer = mSensorMeasurements.get(seriesEntity);
            if (measurementsBuffer.getBinSize() > 0) {
                storeMeasurement(session, seriesEntity, measurementsBuffer);
            }
        }
    }

    /**
     * Obtains all the measurements from a measurement series.
     *
     * @param session needed to retrieve all measurements from the database.
     * @param series  that needs to retrieve all its measurements.
     * @return {@link List} of {@link SensorMeasurementEntity}
     */
    List<SensorMeasurementEntity> getAllMeasurementsFromSeries(@NonNull final DaoSession session,
                                                               @NonNull final SensorMeasurementSeriesEntity series) {
        final SensorMeasurementEntityDao dao = session.getSensorMeasurementEntityDao();
        final QueryBuilder<SensorMeasurementEntity> queryBuilder = dao.queryBuilder();
        queryBuilder.where(SensorMeasurementEntityDao.Properties.Measurement_series_id.eq(series.getId()));
        final List<SensorMeasurementEntity> result = queryBuilder.list();
        Collections.sort(result);
        return result;
    }

    private static class SensorMeasurementsBuffer {
        @NonNull
        private final List<Float> measurements = new ArrayList<>();
        @NonNull
        private DateTime firstElementTime;

        private SensorMeasurementsBuffer() {
            this.firstElementTime = DateTime.now();
        }

        private void addMeasurement(final float measurement) {
            if (measurements.isEmpty()) {
                firstElementTime = DateTime.now();
            }
            this.measurements.add(measurement);
        }

        private float getMidSensorValue() {
            if (measurements.size() == 1) {
                return measurements.get(0);
            }
            Collections.sort(measurements);
            final int midElement = measurements.size() / 2;
            if (measurements.size() % 2 == 1) {
                return measurements.get(midElement);
            } else {
                return (byte) ((measurements.get(midElement - 1) + measurements.get(midElement)) / 2);
            }
        }

        private short getBinSize() {
            return (short) this.measurements.size();
        }

        private void clear() {
            this.measurements.clear();
        }
    }
}