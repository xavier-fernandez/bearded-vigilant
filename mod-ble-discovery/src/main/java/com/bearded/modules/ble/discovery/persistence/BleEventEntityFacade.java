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
import com.bearded.modules.ble.discovery.domain.BleEventEntity;
import com.bearded.modules.ble.discovery.domain.BleEventSeriesEntity;
import com.bearded.modules.ble.discovery.persistence.dao.BleEventEntityDao;
import com.bearded.modules.ble.discovery.persistence.dao.DaoSession;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;

class BleEventEntityFacade {

    @NonNull
    private static final String TAG = BleEventEntityFacade.class.getSimpleName();
    @NonNull
    private final Map<BleEventSeriesEntity, EventBuffer> mEventBuffers;
    @NonNull
    private final Integer mTimeoutMillis;

    public BleEventEntityFacade(final int timeoutMillis) {
        mEventBuffers = Collections.synchronizedMap(
                new HashMap<BleEventSeriesEntity, EventBuffer>());
        if (timeoutMillis <= 0) {
            throw new IllegalArgumentException(
                    String.format("%s: Constructor -> TimeoutMillis needs to be a positive number.", TAG));
        }
        mTimeoutMillis = timeoutMillis;
    }

    /**
     * Stores a measurement in the database.
     *
     * @param session needed to store the measurement.
     * @param series  of the measurement.
     * @param rssi    received.
     */
    public synchronized void addMeasurement(@NonNull final DaoSession session,
                                            @NonNull final BleEventSeriesEntity series,
                                            final byte rssi) {
        EventBuffer buffer = mEventBuffers.get(series);
        if (buffer == null) {
            //The first inserted element will be written inside the database.
            buffer = new EventBuffer();
            buffer.addReceivedSignalStrength(rssi);
            mEventBuffers.put(series, buffer);
            storeEvent(session, series, buffer);
        } else {
            buffer.addReceivedSignalStrength(rssi);
            if (TimeUtils.millisecondsFromNow(buffer.firstElementTime) > mTimeoutMillis) {
                storeEvent(session, series, buffer);
            }
        }
    }

    private void storeEvent(@NonNull final DaoSession session,
                            @NonNull final BleEventSeriesEntity series,
                            @NonNull final EventBuffer buffer) {
        final BleEventEntity eventEntity = new BleEventEntity();
        eventEntity.setStartTimestamp(TimeUtils.timestampToISOString(buffer.firstElementTime));
        eventEntity.setEndTimestamp(TimeUtils.nowToISOString());
        eventEntity.setReceivedSignalStrength(buffer.getMidReceivedSignalStrength());
        eventEntity.setBleEventSeriesEntity(series);
        eventEntity.setBinSize(buffer.getBinSize());
        session.insert(eventEntity);
        buffer.clear();
        Log.d(TAG, String.format("storeEvent -> The following event was inserted in the database -> %s",
                eventEntity.toJsonObject().toString()));
    }

    /**
     * Inserts all open events buffer inside the database.
     *
     * @param session needed to insert all open events into the database.
     */
    void storeAllOpenEvents(@NonNull final DaoSession session) {
        for (final BleEventSeriesEntity seriesEntity : mEventBuffers.keySet()) {
            final EventBuffer buffer = mEventBuffers.get(seriesEntity);
            if (buffer.getBinSize() > 0) {
                storeEvent(session, seriesEntity, buffer);
            }
        }
    }

    /**
     * Obtains all the measurements from a {@link BleEventSeriesEntity}
     *
     * @param session needed to retrieve all events from the database.
     * @param series  that needs to retrieve all its events.
     * @return {@link List} of {@link BleEventEntity}
     */
    @NonNull
    List<BleEventEntity> getAllEventsFromSeries(@NonNull final DaoSession session,
                                                @NonNull final BleEventSeriesEntity series) {
        final BleEventEntityDao dao = session.getBleEventEntityDao();
        final QueryBuilder<BleEventEntity> queryBuilder = dao.queryBuilder();
        queryBuilder.where(BleEventEntityDao.Properties.EventSeriesId.eq(series.getId()));
        final List<BleEventEntity> result = queryBuilder.list();
        Collections.sort(result);
        return result;
    }

    private static class EventBuffer {
        @NonNull
        private final List<Byte> mRssiBuffer = new ArrayList<>();
        @NonNull
        private DateTime firstElementTime;

        private EventBuffer() {
            this.firstElementTime = DateTime.now();
        }

        private void addReceivedSignalStrength(final byte rssi) {
            if (mRssiBuffer.isEmpty()) {
                firstElementTime = DateTime.now();
            }
            this.mRssiBuffer.add(rssi);
        }

        private byte getMidReceivedSignalStrength() {
            float sum = 0;
            for (final byte measurement : this.mRssiBuffer) {
                sum += measurement;
            }
            return (byte) (sum / this.mRssiBuffer.size());
        }

        private short getBinSize() {
            return (short) this.mRssiBuffer.size();
        }

        private void clear() {
            this.mRssiBuffer.clear();
        }
    }
}