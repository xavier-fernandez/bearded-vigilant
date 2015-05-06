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

package com.bearded.common.location;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class LocationDataManager {

    @NonNull
    private static final String TAG = LocationDataManager.class.getSimpleName();
    @Nullable
    private static LocationDataManager mInstance = null;
    @NonNull
    private final LocationDataListener mLocationDataListener;

    private LocationDataManager(@NonNull final Context context) {
        mLocationDataListener = new LocationDataListener(context);
    }

    /**
     * Returns the singleton class instance. init(context) method needs to be
     * called before the first getInstance(Context) class.
     *
     * @return the singleton {@link LocationDataManager} instance.
     */
    @NonNull
    public synchronized static LocationDataManager getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException(
                    TAG + ": getInstance() -> LocationDataManager needs to be initialized " +
                            "before using it. (HINT -> call init(context))");
        }
        return mInstance;
    }

    /**
     * Initializes the {@link LocationDataManager}, this methods needs to be called before the
     * first getInstance() call.
     *
     * @param context needed to retrieve the location updates.
     */
    public synchronized static void init(@NonNull final Context context) {
        if (mInstance == null) {
            mInstance = new LocationDataManager(context);
        } else {
            Log.w(TAG, "init -> %s has been already initialized.");
        }
    }

    /**
     * @see LocationDataListener#registerForLocationUpdates()
     */
    private void registerForLocationUpdates() {
        mLocationDataListener.registerForLocationUpdates();
    }

    /**
     * @see LocationDataListener#getLastTimedLocation()
     */
    @Nullable
    public TimedLocation getLastTimedLocation() {
        return mLocationDataListener.getLastTimedLocation();
    }

    /**
     * @see LocationDataListener#getLocationProviders()
     */
    @Nullable
    public Iterable<LocationProviderWithStatus> getLocationProviders() {
        return mLocationDataListener.getLocationProviders();
    }
}