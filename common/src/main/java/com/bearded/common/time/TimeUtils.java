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
package com.bearded.common.time;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;

/**
 * Class that contains several methods for making the time management easier.
 */
public abstract class TimeUtils {

    private static final String TAG = TimeUtils.class.getSimpleName();

    /**
     * Obtains the number of milliseconds from a given {@link DateTime}
     *
     * @param dateTime that will be used to calculate the difference.
     * @return {@link long} with the number of milliseconds.
     */
    public static long millisecondsFromNow(@NonNull DateTime dateTime) {
        return millisecondsFromNow(dateTime.getMillis());
    }

    /**
     * Obtains the number of milliseconds from a given timestamp.
     *
     * @param timestamp in UTC.
     * @return {@link long} with the number of milliseconds.
     */
    public static long millisecondsFromNow(long timestamp) {
        return DateTime.now().getMillis() - timestamp;
    }

    /**
     * Obtains an ISO 8601 {@link java.lang.String} from now.
     *
     * @return {@link java.lang.String} with the date in seconds following the ISO 8601 conventions.
     */
    @NonNull
    public static String nowToISOString() {
        return DateTime.now().toString();
    }

    /**
     * Convert a given timestamp into ISO 8601 {@link java.lang.String}
     *
     * @param datetime that will be converted to the ISO 8601 convention.
     * @return {@link java.lang.String} with the date in seconds following the ISO 8601 conventions.
     */
    @NonNull
    public static String timestampToISOString(@NonNull DateTime datetime) {
        return timestampToISOString(datetime.getMillis());
    }

    /**
     * Convert a given timestamp into ISO 8601 {@link java.lang.String}
     *
     * @param timestamp that will be converted. Must be a positive number.
     * @return {@link java.lang.String} with the date in seconds following the ISO 8601 conventions.
     */
    @NonNull
    public static String timestampToISOString(long timestamp) {
        if (timestamp < 0) {
            throw new IllegalArgumentException(String.format("%s: timestampToISOString -> Received a negative timestamp", TAG));
        }
        return (new DateTime(timestamp)).toString();
    }
}