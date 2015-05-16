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
import com.bearded.modules.sensor.internal.domain.SensorEntity;
import com.bearded.modules.sensor.internal.domain.SensorMeasurementEntity;
import com.bearded.modules.sensor.internal.domain.SensorMeasurementSeriesEntity;
import com.bearded.modules.sensor.internal.persistence.dao.DaoSession;
import com.bearded.modules.sensor.internal.persistence.dao.SensorMeasurementEntityDao;
import com.bearded.modules.sensor.internal.persistence.dao.SensorMeasurementSeriesEntityDao;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;

class SensorMeasurementSeriesEntityFacade {

    @NonNull
    private static final String TAG = SensorMeasurementSeriesEntityFacade.class.getSimpleName();

    @NonNull
    private final Map<SensorEntity, SensorMeasurementSeriesEntity> mSensorMeasurementSeries;

    SensorMeasurementSeriesEntityFacade() {
        mSensorMeasurementSeries = Collections.synchronizedMap(new HashMap<SensorEntity, SensorMeasurementSeriesEntity>());
    }

    /**
     * Obtains an active measurement series from a given sensor.
     * Creates a measurement series in case no active measurement series is found.
     *
     * @param session      needed to query, insert or update the measurement series.
     * @param sensorEntity of the measurement series.
     * @return {@link SensorMeasurementSeriesEntity} with the measurement series.
     */
    @NonNull
    public SensorMeasurementSeriesEntity getActiveMeasurementSeries(@NonNull final DaoSession session,
                                                                    @NonNull final SensorEntity sensorEntity) {
        if (mSensorMeasurementSeries.get(sensorEntity) != null) {
            return mSensorMeasurementSeries.get(sensorEntity);
        }
        final SensorMeasurementSeriesEntityDao dao = session.getSensorMeasurementSeriesEntityDao();
        final QueryBuilder<SensorMeasurementSeriesEntity> queryBuilder = dao.queryBuilder();
        queryBuilder.where(SensorMeasurementSeriesEntityDao.Properties.EndTimestamp.isNull());
        queryBuilder.where(SensorMeasurementSeriesEntityDao.Properties.Sensor_id.eq(sensorEntity.getId()));
        final List<SensorMeasurementSeriesEntity> query = queryBuilder.list();
        if (!query.isEmpty()) {
            for (final SensorMeasurementSeriesEntity series : query) {
                updateMeasurementSeriesEndTimestamp(session, series);
            }
        }
        return insertMeasurementSeries(session, sensorEntity);
    }

    /**
     * Obtains all measurement series from a given sensor.
     *
     * @param session      needed to query, insert or update the measurement series.
     * @param sensorEntity of the measurement series.
     * @return {@link SensorMeasurementSeriesEntity} with the measurement series.
     */
    @NonNull
    public List<SensorMeasurementSeriesEntity> getAllClosedMeasurementSeriesFromSensor(@NonNull final DaoSession session,
                                                                                       @NonNull final SensorEntity sensorEntity) {
        final SensorMeasurementSeriesEntityDao dao = session.getSensorMeasurementSeriesEntityDao();
        final QueryBuilder<SensorMeasurementSeriesEntity> queryBuilder = dao.queryBuilder();
        queryBuilder.where(SensorMeasurementSeriesEntityDao.Properties.Sensor_id.eq(sensorEntity.getId()));
        queryBuilder.where(SensorMeasurementSeriesEntityDao.Properties.EndTimestamp.isNotNull());
        return queryBuilder.listLazy();
    }

    /**
     * Inserts a measurement series from a given sensor.
     *
     * @param session      needed to insert the measurement series.
     * @param sensorEntity of the inserted measurement series.
     * @return {@link SensorMeasurementSeriesEntity} with the measurement series.
     */
    @NonNull
    private SensorMeasurementSeriesEntity insertMeasurementSeries(@NonNull final DaoSession session,
                                                                  @NonNull final SensorEntity sensorEntity) {
        final SensorMeasurementSeriesEntity measurementSeries = new SensorMeasurementSeriesEntity();
        measurementSeries.setStartTimestamp(TimeUtils.nowToISOString());
        measurementSeries.setSensorEntity(sensorEntity);
        session.insert(measurementSeries);
        mSensorMeasurementSeries.put(sensorEntity, measurementSeries);
        Log.d(TAG, "insertMeasurementSeries -> Insert measurement series -> " + measurementSeries.toJsonObject().toString());
        return measurementSeries;
    }

    /**
     * Updates the end timestamp of all measurement series.
     *
     * @param session needed to update measurement series.
     */
    public void updateAllMeasurementSeriesEndTimestamp(@NonNull final DaoSession session) {
        synchronized (this) {
            final SensorMeasurementSeriesEntityDao seriesDao = session.getSensorMeasurementSeriesEntityDao();
            final QueryBuilder<SensorMeasurementSeriesEntity> queryBuilder = seriesDao.queryBuilder();
            queryBuilder.where(SensorMeasurementSeriesEntityDao.Properties.EndTimestamp.isNull());
            final List<SensorMeasurementSeriesEntity> outdatedSeriesQuery = queryBuilder.list();
            for (final SensorMeasurementSeriesEntity series : outdatedSeriesQuery) {
                updateMeasurementSeriesEndTimestamp(session, series);
            }
            mSensorMeasurementSeries.clear();
        }
    }

    /**
     * Updates the timestamp of the given measurement series.
     *
     * @param session needed to update measurement series.
     * @param series  which timestamp is going to be updated.
     */
    private void updateMeasurementSeriesEndTimestamp(@NonNull final DaoSession session,
                                                     @NonNull final SensorMeasurementSeriesEntity series) {
        final SensorMeasurementEntityDao measurementDao = session.getSensorMeasurementEntityDao();
        final QueryBuilder<SensorMeasurementEntity> measurementQuery = measurementDao.queryBuilder();
        measurementQuery.where(SensorMeasurementEntityDao.Properties.Measurement_series_id.eq(series.getId()));
        measurementQuery.orderDesc(SensorMeasurementEntityDao.Properties.EndTimestamp);
        final List<SensorMeasurementEntity> queryResult = measurementQuery.list();
        if (queryResult != null && queryResult.size() > 0) {
            series.setEndTimestamp(queryResult.get(0).getEndTimestamp());
        } else {
            series.setEndTimestamp(TimeUtils.nowToISOString());
        }
        session.update(series);
    }
}