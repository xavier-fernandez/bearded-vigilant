package com.bearded.modules.ble.discovery.persistence;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    public BleEventEntityFacade(int timeoutMillis) {
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
    public synchronized void addMeasurement(@NonNull DaoSession session,
                                            @NonNull BleEventSeriesEntity series,
                                            byte rssi) {
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

    private void storeEvent(@NonNull DaoSession session,
                            @NonNull BleEventSeriesEntity series,
                            @NonNull EventBuffer buffer) {
        final BleEventEntity eventEntity = new BleEventEntity();
        eventEntity.setStartTimestamp(TimeUtils.timestampToISOString(buffer.firstElementTime));
        eventEntity.setEndTimestamp(TimeUtils.nowToISOString());
        eventEntity.setMedianReceivedSignalStrength(buffer.getMedianReceivedSignalStrength());
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
    void storeAllOpenEvents(@NonNull DaoSession session) {
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
    @Nullable
    List<BleEventEntity> getAllEventsFromSeries(@NonNull DaoSession session,
                                                @NonNull BleEventSeriesEntity series) {
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

        private void addReceivedSignalStrength(byte rssi) {
            if (mRssiBuffer.isEmpty()) {
                firstElementTime = DateTime.now();
            }
            this.mRssiBuffer.add(rssi);
        }

        private byte getMedianReceivedSignalStrength() {
            if (mRssiBuffer.size() == 1) {
                return mRssiBuffer.get(0);
            }
            Collections.sort(mRssiBuffer);
            final int midElement = mRssiBuffer.size() / 2;
            if (mRssiBuffer.size() % 2 == 1) {
                return mRssiBuffer.get(midElement);
            } else {
                return (byte) ((mRssiBuffer.get(midElement - 1) + mRssiBuffer.get(midElement)) / 2);
            }
        }

        private short getBinSize() {
            return (short) this.mRssiBuffer.size();
        }

        private void clear() {
            this.mRssiBuffer.clear();
        }
    }
}