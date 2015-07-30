package com.bearded.common.location;

import android.location.Location;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;

public class TimedLocation {

    @NonNull
    private final Location mLocation;
    @NonNull
    private final DateTime mDatetime;

    public TimedLocation(@NonNull Location location) {
        mLocation = location;
        mDatetime = DateTime.now();
    }

    /**
     * Returns the last location.
     *
     * @return {@link Location} with the last location.
     */
    @NonNull
    public Location getLocation() {
        return mLocation;
    }

    /**
     * Returns the time of the last location.
     *
     * @return {@link DateTime} with the last time.
     */
    @NonNull
    public DateTime getTime() {
        return mDatetime;
    }
}
