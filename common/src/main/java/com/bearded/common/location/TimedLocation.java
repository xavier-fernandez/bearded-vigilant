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
import android.support.annotation.NonNull;

import org.joda.time.DateTime;

public class TimedLocation {

    @NonNull
    private final Location mLocation;
    @NonNull
    private final DateTime mDatetime;

    public TimedLocation(@NonNull final Location location) {
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
