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

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bearded.common.location.LocationDataManager;
import com.bearded.common.location.TimedLocation;
import com.bearded.common.time.TimeUtils;
import com.bearded.modules.ble.discovery.domain.LocationEntity;
import com.bearded.modules.ble.discovery.persistence.dao.DaoSession;
import com.bearded.modules.ble.discovery.persistence.dao.LocationEntityDao;

import org.joda.time.DateTime;

import de.greenrobot.dao.query.QueryBuilder;

import static com.bearded.modules.ble.discovery.persistence.dao.LocationEntityDao.Properties.Id;

class BleLocationEntityFacade {

    @NonNull
    private static final String TAG = BleLocationEntityFacade.class.getSimpleName();
    private static final int LOCATION_BIN_SIZE_MS = 15 * 1000; // 15 seconds
    @Nullable
    private LocationEntity mLastInsertedLocationEntity = null;
    @Nullable
    private DateTime mLastInsertedLocationTime;

    /**
     * Obtains the last inserted {@link LocationEntity}.
     * If the last {@link LocationEntity} is null or its obsolete it insert a new location entity.
     *
     * @param session needed to insert a new location entity, if necessary.
     * @return {@link LocationEntity} if an up to date location is found.
     */
    @Nullable
    LocationEntity getActiveLocation(@NonNull final DaoSession session) {
        final TimedLocation lastLocation = LocationDataManager.getInstance().getLastTimedLocation();
        if (lastLocation == null) {
            return null;
        }
        if (mLastInsertedLocationTime == null) {
            final TimedLocation location = LocationDataManager.getInstance().getLastTimedLocation();
            if (location == null) {
                Log.w(TAG, "getActiveLocation -> No valid location found.");
                return null;
            }
            return insertLocation(session, location);
        }
        if (TimeUtils.millisecondsFromNow(mLastInsertedLocationTime) > LOCATION_BIN_SIZE_MS
                && TimeUtils.millisecondsFromNow(lastLocation.getTime()) < LOCATION_BIN_SIZE_MS) {
            return insertLocation(session, lastLocation);
        }
        return mLastInsertedLocationEntity;
    }

    /**
     * Inserts a given location in the database.
     *
     * @param session       needed to insert the location inside the database.
     * @param timedLocation that will be inserted inside the database.
     * @return the inserted {@link LocationEntity}
     */
    @NonNull
    private LocationEntity insertLocation(@NonNull final DaoSession session,
                                          @NonNull final TimedLocation timedLocation) {
        Log.d(TAG, String.format("insertLocation -> Inserting location %s.", timedLocation));
        final LocationEntity locationEntity = new LocationEntity();
        final Location location = timedLocation.getLocation();
        locationEntity.setLatitude(location.getLatitude());
        locationEntity.setLongitude(location.getLongitude());
        locationEntity.setAccuracyInMeters(location.getAccuracy());
        locationEntity.setSpeedInMetersSecond(location.getSpeed());
        session.insert(locationEntity);
        mLastInsertedLocationTime = DateTime.now();
        return locationEntity;
    }

    /**
     * Removes all the outdated location entities from the database.
     *
     * @param session needed to purge all the outdated locations from the database.
     */
    void purgeAllOutdatedLocationEntities(@NonNull final DaoSession session) {
        final LocationEntityDao dao = session.getLocationEntityDao();
        final QueryBuilder<LocationEntity> queryBuilder = dao.queryBuilder();
        if (mLastInsertedLocationEntity != null) {
            queryBuilder.where(Id.notEq(mLastInsertedLocationEntity.getId()));
        }
        for (final LocationEntity outdatedLocation : queryBuilder.list()) {
            session.delete(outdatedLocation);
        }
    }
}