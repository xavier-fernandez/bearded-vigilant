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

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bearded.common.location.LocationDataManager;
import com.bearded.common.location.TimedLocation;
import com.bearded.modules.sensor.internal.domain.LocationEntity;
import com.bearded.modules.sensor.internal.persistence.dao.DaoSession;

import java.util.Stack;

class InternalSensorLocationEntityFacade {

    @NonNull
    private static final String TAG = InternalSensorLocationEntityFacade.class.getSimpleName();
    @NonNull
    private final Stack<TimedLocation> mLocationBuffer = new Stack<>();
    @Nullable
    private LocationEntity mLastLocation = null;

    @Nullable
    LocationEntity getActiveLocation(@NonNull final DaoSession session) {
        if (mLastLocation == null) {
            final TimedLocation location = LocationDataManager.getInstance().getLastTimedLocation();
            if (location == null) {
                Log.w(TAG, "getActiveLocation -> No valid location found.");
                return null;
            }
            insertLocation(session, location);
        }

        return null;
    }

    /**
     * Inserts a given location in the database.
     * @param session needed to insert the location inside the database.
     * @param timedLocation that will be inserted inside the database.
     * @return the inserted {@link LocationEntity}
     */
    @NonNull
    private LocationEntity insertLocation(@NonNull final DaoSession session,
                                          @NonNull final TimedLocation timedLocation) {
        Log.d(TAG, "insertLocation -> Inserting location %s.");
        final LocationEntity locationEntity = new LocationEntity();
        final Location location = timedLocation.getLocation();
        locationEntity.setLatitude(location.getLatitude());
        locationEntity.setLongitude(location.getLongitude());
        locationEntity.setAccuracyInMeters(location.getAccuracy());
        locationEntity.setSpeedInMetersSecond(location.getSpeed());
        session.insert(locationEntity);
        return locationEntity;
    }
}