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

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bearded.common.annotation.LocationProviderStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LocationManager implements LocationListener {

    @NonNull
    private static final String TAG = LocationManager.class.getSimpleName();
    @Nullable
    private static LocationManager mInstance = null;
    @NonNull
    private final Map<String, LocationProviderWithStatus> mProviders;
    @Nullable
    private TimedLocation mLastTimedLocation = null;

    private LocationManager() {
        mProviders = Collections.synchronizedMap(new HashMap<String, LocationProviderWithStatus>());
    }

    /**
     * Returns the singleton class instance.
     *
     * @return the singleton {@link LocationManager} instance.
     */
    @NonNull
    public synchronized static LocationManager getInstance() {
        if (mInstance == null) {
            mInstance = new LocationManager();
        }
        return mInstance;
    }

    /**
     * Returns the location provider.
     *
     * @return {@link Iterable} of {@link LocationProviderWithStatus} with all the providers.
     */
    @Nullable
    public Iterable<LocationProviderWithStatus> getLocationProviders() {
        return Collections.unmodifiableCollection(mProviders.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLocationChanged(@NonNull final Location location) {
        mLastTimedLocation = new TimedLocation(location);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStatusChanged(@NonNull final String provider,
                                @LocationProviderStatus final int status,
                                @Nullable final Bundle extras) {
        mProviders.get(provider).setStatus(status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onProviderEnabled(@NonNull final String provider) {
        Log.d(TAG, "onProviderEnabled -> The provider with name %s was enabled.");
        mProviders.put(provider, new LocationProviderWithStatus(provider));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onProviderDisabled(@NonNull final String provider) {
        Log.d(TAG, "onProviderEnabled -> The provider with name %s was disabled.");
        mProviders.remove(provider);
    }
}