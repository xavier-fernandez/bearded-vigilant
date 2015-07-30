package com.bearded.common.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bearded.common.annotation.LocationProviderStatus;
import com.bearded.common.time.TimeUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class LocationDataListener implements LocationListener {

    @NonNull
    private static final String TAG = LocationDataManager.class.getSimpleName();
    private static final long MIN_LOCATION_UPDATE_TIME_MILLISECONDS = 5 * 1000; // 5 seconds
    private static final long MIN_DISTANCE_METERS = 10;
    @NonNull
    private final Map<String, LocationProviderWithStatus> mProviders;
    @NonNull
    private final LocationManager mLocationManager;
    @Nullable
    private TimedLocation mLastTimedLocation = null;

    LocationDataListener(@NonNull Context context) {
        mProviders = Collections.synchronizedMap(new HashMap<String, LocationProviderWithStatus>());
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Obtains the last timed location. In case the last location is obsolete, it will try to
     * register for location updates again.
     *
     * @return {@link TimedLocation} with the last location. <code>null</code> if it is unknown.
     */
    @Nullable
    TimedLocation getLastTimedLocation() {
        if (mLastTimedLocation == null ||
                TimeUtils.millisecondsFromNow(mLastTimedLocation.getTime())
                        > MIN_LOCATION_UPDATE_TIME_MILLISECONDS * 2) {
            registerForLocationUpdates();
        }
        return mLastTimedLocation;
    }

    /**
     * Assign LocationListener to LocationManager in order to receive location updates.
     * Acquiring provider that is used for location updates will also be covered later.
     * Instead of LocationListener, PendingIntent can be assigned, also instead of
     * provider name, criteria can be used, but we won't use those approaches now.
     */
    void registerForLocationUpdates() {
        if (mLocationManager.getAllProviders().isEmpty()) {
            Log.e(TAG, "registerForLocationUpdates -> Can not find location providers.");
        } else {
            for (final String locationProvider : mLocationManager.getAllProviders()) {
                mLocationManager.requestLocationUpdates(locationProvider,
                        MIN_LOCATION_UPDATE_TIME_MILLISECONDS, MIN_DISTANCE_METERS, this);
            }
        }
    }

    /**
     * Returns the location provider.
     *
     * @return {@link Iterable} of {@link LocationProviderWithStatus} with all the providers.
     */
    @NonNull
    Iterable<LocationProviderWithStatus> getLocationProviders() {
        return Collections.unmodifiableCollection(mProviders.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        mLastTimedLocation = new TimedLocation(location);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStatusChanged(@NonNull String provider,
                                @LocationProviderStatus int status,
                                @Nullable Bundle extras) {
        mProviders.get(provider).setStatus(status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.d(TAG, "onProviderEnabled -> The provider with name %s was enabled.");
        mProviders.put(provider, new LocationProviderWithStatus(provider));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Log.d(TAG, "onProviderEnabled -> The provider with name %s was disabled.");
        mProviders.remove(provider);
    }
}