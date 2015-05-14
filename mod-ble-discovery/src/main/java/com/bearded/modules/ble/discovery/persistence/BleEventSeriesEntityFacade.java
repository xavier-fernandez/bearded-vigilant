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

import android.support.annotation.NonNull;
import android.util.Log;

import com.bearded.common.time.TimeUtils;
import com.bearded.modules.ble.discovery.domain.BleDeviceEntity;
import com.bearded.modules.ble.discovery.domain.BleEventEntity;
import com.bearded.modules.ble.discovery.domain.BleEventSeriesEntity;
import com.bearded.modules.ble.discovery.persistence.dao.BleEventEntityDao;
import com.bearded.modules.ble.discovery.persistence.dao.BleEventSeriesEntityDao;
import com.bearded.modules.ble.discovery.persistence.dao.DaoSession;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;

class BleEventSeriesEntityFacade {

    @NonNull
    private static final String TAG = BleEventSeriesEntityFacade.class.getSimpleName();

    @NonNull
    private final Map<BleDeviceEntity, BleEventSeriesEntity> mBleEventSeries;

    BleEventSeriesEntityFacade() {
        mBleEventSeries = Collections.synchronizedMap(new HashMap<BleDeviceEntity, BleEventSeriesEntity>());
    }

    /**
     * Obtains an active event series from a given {@link BleEventEntity}.
     * Creates a measurement series in case no active event series is found.
     *
     * @param session      needed to query, insert or update the event series.
     * @param deviceEntity of the event series.
     * @return {@link BleEventSeriesEntity} with the event series.
     */
    @NonNull
    public BleEventSeriesEntity getActiveEventSeries(@NonNull final DaoSession session,
                                                     @NonNull final BleDeviceEntity deviceEntity) {
        if (mBleEventSeries.get(deviceEntity) != null) {
            return mBleEventSeries.get(deviceEntity);
        }
        final BleEventSeriesEntityDao dao = session.getBleEventSeriesEntityDao();
        final QueryBuilder<BleEventSeriesEntity> queryBuilder = dao.queryBuilder();
        queryBuilder.where(BleEventSeriesEntityDao.Properties.EndTimestamp.isNull());
        queryBuilder.where(BleEventSeriesEntityDao.Properties.BleDeviceId.eq(deviceEntity.getId()));
        final List<BleEventSeriesEntity> query = queryBuilder.list();
        if (!query.isEmpty()) {
            for (final BleEventSeriesEntity series : query) {
                updateEventSeriesEndTimestamp(session, series);
            }
        }
        return insertEventSeries(session, deviceEntity);
    }

    /**
     * Obtains all event series from a given sensor.
     *
     * @param session      needed to query, insert or update the event series.
     * @param deviceEntity of the event series.
     * @return {@link BleEventSeriesEntity} with the event series.
     */
    @NonNull
    public List<BleEventSeriesEntity> getAllClosedEventSeriesFromDevice(@NonNull final DaoSession session,
                                                                        @NonNull final BleDeviceEntity deviceEntity) {
        final BleEventSeriesEntityDao dao = session.getBleEventSeriesEntityDao();
        final QueryBuilder<BleEventSeriesEntity> queryBuilder = dao.queryBuilder();
        queryBuilder.where(BleEventSeriesEntityDao.Properties.BleDeviceId.eq(deviceEntity.getId()));
        queryBuilder.where(BleEventSeriesEntityDao.Properties.EndTimestamp.isNotNull());
        return queryBuilder.listLazy();
    }

    /**
     * Inserts a event series from a given sensor.
     *
     * @param session      needed to insert the event series.
     * @param deviceEntity of the inserted event series.
     * @return {@link BleEventSeriesEntity} with the event series.
     */
    @NonNull
    private BleEventSeriesEntity insertEventSeries(@NonNull final DaoSession session,
                                                   @NonNull final BleDeviceEntity deviceEntity) {
        final BleEventSeriesEntity eventSeries = new BleEventSeriesEntity();
        eventSeries.setStartTimestamp(TimeUtils.nowToISOString());
        eventSeries.setBleDeviceEntity(deviceEntity);
        session.insert(eventSeries);
        mBleEventSeries.put(deviceEntity, eventSeries);
        Log.d(TAG, "insertEventSeries -> Insert event series -> " + eventSeries.toJsonObject().toString());
        return eventSeries;
    }

    /**
     * Updates the end timestamp of all event series.
     *
     * @param session needed to update event series.
     */
    public void updateAllEventSeriesEndTimestamp(@NonNull final DaoSession session) {
        synchronized (this) {
            final BleEventSeriesEntityDao seriesDao = session.getBleEventSeriesEntityDao();
            final QueryBuilder<BleEventSeriesEntity> queryBuilder = seriesDao.queryBuilder();
            queryBuilder.where(BleEventSeriesEntityDao.Properties.EndTimestamp.isNull());
            final List<BleEventSeriesEntity> outdatedSeriesQuery = queryBuilder.list();
            for (final BleEventSeriesEntity series : outdatedSeriesQuery) {
                updateEventSeriesEndTimestamp(session, series);
            }
            mBleEventSeries.clear();
        }
    }

    /**
     * Updates the timestamp of the given event series.
     *
     * @param session needed to update event series.
     * @param series  which timestamp is going to be updated.
     */
    private void updateEventSeriesEndTimestamp(@NonNull final DaoSession session,
                                               @NonNull final BleEventSeriesEntity series) {
        final BleEventEntityDao measurementDao = session.getBleEventEntityDao();
        final QueryBuilder<BleEventEntity> measurementQuery = measurementDao.queryBuilder();
        measurementQuery.where(BleEventEntityDao.Properties.EventSeriesId.eq(series.getId()));
        measurementQuery.orderDesc(BleEventEntityDao.Properties.EndTimestamp);
        final List<BleEventEntity> queryResult = measurementQuery.list();
        if (queryResult != null && queryResult.size() > 0) {
            series.setEndTimestamp(queryResult.get(0).getEndTimestamp());
        } else {
            series.setEndTimestamp(TimeUtils.nowToISOString());
        }
        session.update(series);
    }
}