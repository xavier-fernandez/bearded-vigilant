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
import com.bearded.modules.sensor.internal.domain.InternalSensorMeasurementEntity;
import com.bearded.modules.sensor.internal.domain.InternalSensorMeasurementSeriesEntity;
import com.bearded.modules.sensor.internal.persistence.dao.DaoSession;
import com.bearded.modules.sensor.internal.persistence.dao.InternalSensorMeasurementEntityDao;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;

class InternalSensorMeasurementEntityFacade {

    @NonNull
    private static final String TAG = InternalSensorMeasurementEntityFacade.class.getSimpleName();
    @NonNull
    private final Map<InternalSensorMeasurementSeriesEntity, SensorMeasurementsBuffer> mSensorMeasurements;
    @NonNull
    private final Integer mTimeoutMillis;

    public InternalSensorMeasurementEntityFacade(final int timeoutMillis) {
        mSensorMeasurements = Collections.synchronizedMap(
                new HashMap<InternalSensorMeasurementSeriesEntity, SensorMeasurementsBuffer>());
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
                                            @NonNull final InternalSensorMeasurementSeriesEntity series,
                                            final float measurement) {
        SensorMeasurementsBuffer measurements = mSensorMeasurements.get(series);
        if (measurements == null) {
            //The first inserted element will be written inside the database.
            measurements = new SensorMeasurementsBuffer();
            mSensorMeasurements.put(series, measurements);
            measurements.addMeasurement(measurement);
            storeMeasurement(session, measurements);
        } else {
            measurements.addMeasurement(measurement);
            if (TimeUtils.millisecondsFromNow(measurements.firstElementTime) > mTimeoutMillis) {
                storeMeasurement(session, measurements);
            }
        }
    }

    private void storeMeasurement(@NonNull final DaoSession session,
                                  @NonNull final SensorMeasurementsBuffer measurements) {
        final InternalSensorMeasurementEntity measurementEntity = new InternalSensorMeasurementEntity();
        measurementEntity.setStartTimestamp(TimeUtils.timestampToISOString(measurements.firstElementTime));
        measurementEntity.setEndTimestamp(TimeUtils.timestampToISOString(DateTime.now()));
        measurementEntity.setBinSize(measurements.getBinSize());
        measurementEntity.setSensorValue(measurements.getMidSensorValue());
        session.getInternalSensorMeasurementEntityDao().insert(measurementEntity);
        measurements.clear();
        Log.d(TAG, String.format("addMeasurement -> The following measurement was inserted in the database -> %s",
                measurementEntity.toJsonObject().toString()));
    }

    /**
     * Obtains all the measurements from a measurement series.
     *
     * @param session needed to retrieve all measurements from the database.
     * @param series  that needs to retrieve all its measurements.
     * @return {@link List} of {@link InternalSensorMeasurementEntity}
     */
    List<InternalSensorMeasurementEntity> obtainAllMeasurementsFromSeries(@NonNull final DaoSession session,
                                                                          @NonNull final InternalSensorMeasurementSeriesEntity series) {
        final InternalSensorMeasurementEntityDao dao = session.getInternalSensorMeasurementEntityDao();
        final QueryBuilder<InternalSensorMeasurementEntity> queryBuilder = dao.queryBuilder();
        queryBuilder.where(InternalSensorMeasurementEntityDao.Properties.Measurement_series_id.eq(series.getId()));
        return queryBuilder.listLazy();
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
            float sum = 0;
            for (final Float measurement : this.measurements) {
                sum += measurement;
            }
            return sum / this.measurements.size();
        }

        private short getBinSize() {
            return (short) this.measurements.size();
        }

        private void clear() {
            this.measurements.clear();
        }
    }
}