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

import com.bearded.common.utils.TimeUtils;
import com.bearded.modules.sensor.internal.domain.InternalSensorEntity;
import com.bearded.modules.sensor.internal.domain.InternalSensorMeasurementEntity;
import com.bearded.modules.sensor.internal.domain.InternalSensorMeasurementSeriesEntity;
import com.bearded.modules.sensor.internal.persistence.dao.DaoSession;
import com.bearded.modules.sensor.internal.persistence.dao.InternalSensorMeasurementEntityDao;
import com.bearded.modules.sensor.internal.persistence.dao.InternalSensorMeasurementSeriesEntityDao;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;

class InternalSensorMeasurementSeriesEntityFacade {

    @NotNull
    private final Map<InternalSensorEntity, InternalSensorMeasurementSeriesEntity> mSensorMeasurementSeries;

    InternalSensorMeasurementSeriesEntityFacade() {
        mSensorMeasurementSeries = Collections.synchronizedMap(new HashMap<InternalSensorEntity, InternalSensorMeasurementSeriesEntity>());
    }

    /**
     * Obtains a measurement series from a given sensor.
     *
     * @param session      needed to query, insert or update the measurement series.
     * @param sensorEntity of the measurement series.
     * @return {@link InternalSensorMeasurementSeriesEntity} with a valid measurement series.
     */
    @NotNull
    public InternalSensorMeasurementSeriesEntity getMeasurementSeries(@NotNull final DaoSession session,
                                                                      @NotNull final InternalSensorEntity sensorEntity) {
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
        final InternalSensorMeasurementSeriesEntity measurementSeries = new InternalSensorMeasurementSeriesEntity();
        measurementSeries.setStartTimestamp(TimeUtils.nowToISOString());
        measurementSeries.setInternalSensorEntity(sensorEntity);
        session.insert(measurementSeries);
        return measurementSeries;
    }

    /**
     * Updates the end timestamp of all measurement series.
     *
     * @param session needed to update measurement series.
     */
    public void updateAllMeasurementSeriesEndTimestamp(@NotNull final DaoSession session) {
        final InternalSensorMeasurementSeriesEntityDao seriesDao = session.getInternalSensorMeasurementSeriesEntityDao();
        final QueryBuilder<InternalSensorMeasurementSeriesEntity> queryBuilder = seriesDao.queryBuilder();
        queryBuilder.where(InternalSensorMeasurementSeriesEntityDao.Properties.EndTimestamp.eq(null));
        final List<InternalSensorMeasurementSeriesEntity> outdatedSeriesQuery = queryBuilder.list();
        for (final InternalSensorMeasurementSeriesEntity series : outdatedSeriesQuery) {
            updateMeasurementSeriesEndTimestamp(session, series);
        }
    }

    /**
     * Updates the timestamp of the given measurement series.
     *
     * @param session needed to update measurement series.
     * @param series  which timestamp is going to be updated.
     */
    private void updateMeasurementSeriesEndTimestamp(@NotNull final DaoSession session,
                                                     @NotNull final InternalSensorMeasurementSeriesEntity series) {
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