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
import com.bearded.modules.sensor.internal.domain.InternalSensorEntity;
import com.bearded.modules.sensor.internal.domain.InternalSensorMeasurementEntity;
import com.bearded.modules.sensor.internal.domain.InternalSensorMeasurementSeriesEntity;
import com.bearded.modules.sensor.internal.persistence.dao.DaoSession;
import com.bearded.modules.sensor.internal.persistence.dao.InternalSensorMeasurementEntityDao;
import com.bearded.modules.sensor.internal.persistence.dao.InternalSensorMeasurementSeriesEntityDao;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;

class InternalSensorMeasurementSeriesEntityFacade {

    @NonNull
    private static final String TAG = InternalSensorMeasurementSeriesEntityFacade.class.getSimpleName();

    @NonNull
    private final Map<InternalSensorEntity, InternalSensorMeasurementSeriesEntity> mSensorMeasurementSeries;

    InternalSensorMeasurementSeriesEntityFacade() {
        mSensorMeasurementSeries = Collections.synchronizedMap(new HashMap<InternalSensorEntity, InternalSensorMeasurementSeriesEntity>());
    }

    /**
     * Obtains an active measurement series from a given sensor.
     * Creates a measurement series in case no active measurement series is found.
     *
     * @param session      needed to query, insert or update the measurement series.
     * @param sensorEntity of the measurement series.
     * @return {@link InternalSensorMeasurementSeriesEntity} with the measurement series.
     */
    @NonNull
    public InternalSensorMeasurementSeriesEntity getActiveMeasurementSeries(@NonNull final DaoSession session,
                                                                            @NonNull final InternalSensorEntity sensorEntity) {
        if (mSensorMeasurementSeries.get(sensorEntity) != null) {
            return mSensorMeasurementSeries.get(sensorEntity);
        }
        final InternalSensorMeasurementSeriesEntityDao dao = session.getInternalSensorMeasurementSeriesEntityDao();
        final QueryBuilder<InternalSensorMeasurementSeriesEntity> queryBuilder = dao.queryBuilder();
        queryBuilder.where(InternalSensorMeasurementSeriesEntityDao.Properties.EndTimestamp.isNull());
        queryBuilder.where(InternalSensorMeasurementSeriesEntityDao.Properties.Sensor_id.eq(sensorEntity.getId()));
        final List<InternalSensorMeasurementSeriesEntity> query = queryBuilder.list();
        if (!query.isEmpty()) {
            for (final InternalSensorMeasurementSeriesEntity series : query) {
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
     * @return {@link InternalSensorMeasurementSeriesEntity} with the measurement series.
     */
    @NonNull
    public List<InternalSensorMeasurementSeriesEntity> getAllClosedMeasurementSeriesFromSensor(@NonNull final DaoSession session,
                                                                                               @NonNull final InternalSensorEntity sensorEntity) {
        final InternalSensorMeasurementSeriesEntityDao dao = session.getInternalSensorMeasurementSeriesEntityDao();
        final QueryBuilder<InternalSensorMeasurementSeriesEntity> queryBuilder = dao.queryBuilder();
        queryBuilder.where(InternalSensorMeasurementSeriesEntityDao.Properties.Sensor_id.eq(sensorEntity.getId()));
        return queryBuilder.listLazy();
    }

    /**
     * Inserts a measurement series from a given sensor.
     *
     * @param session      needed to insert the measurement series.
     * @param sensorEntity of the inserted measurement series.
     * @return {@link InternalSensorMeasurementSeriesEntity} with the measurement series.
     */
    @NonNull
    private InternalSensorMeasurementSeriesEntity insertMeasurementSeries(@NonNull final DaoSession session,
                                                                          @NonNull final InternalSensorEntity sensorEntity) {
        final InternalSensorMeasurementSeriesEntity measurementSeries = new InternalSensorMeasurementSeriesEntity();
        measurementSeries.setStartTimestamp(TimeUtils.nowToISOString());
        measurementSeries.setInternalSensorEntity(sensorEntity);
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
            final InternalSensorMeasurementSeriesEntityDao seriesDao = session.getInternalSensorMeasurementSeriesEntityDao();
            final QueryBuilder<InternalSensorMeasurementSeriesEntity> queryBuilder = seriesDao.queryBuilder();
            queryBuilder.where(InternalSensorMeasurementSeriesEntityDao.Properties.EndTimestamp.isNull());
            final List<InternalSensorMeasurementSeriesEntity> outdatedSeriesQuery = queryBuilder.list();
            for (final InternalSensorMeasurementSeriesEntity series : outdatedSeriesQuery) {
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
                                                     @NonNull final InternalSensorMeasurementSeriesEntity series) {
        final InternalSensorMeasurementEntityDao measurementDao = session.getInternalSensorMeasurementEntityDao();
        final QueryBuilder<InternalSensorMeasurementEntity> measurementQuery = measurementDao.queryBuilder();
        measurementQuery.where(InternalSensorMeasurementEntityDao.Properties.Measurement_series_id.eq(series.getId()));
        measurementQuery.orderDesc(InternalSensorMeasurementEntityDao.Properties.EndTimestamp);
        final InternalSensorMeasurementEntity measurementEntities = measurementQuery.unique();
        if (measurementEntities != null) {
            series.setEndTimestamp(measurementEntities.getEndTimestamp());
            session.update(series);
        }
    }
}