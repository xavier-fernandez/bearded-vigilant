package com.bearded.common.utils;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

/**
 * Class that contains several methods for making the time management easier.
 */
public abstract class TimeUtils {

    private static final String TAG = TimeUtils.class.getSimpleName();

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.US);

    static {
        final TimeZone timeZone = TimeZone.getTimeZone("UTC");
        dateFormat.setTimeZone(timeZone);
    }

    /**
     * Convert a given timestamp into a ISO 8601 {@link java.lang.String}
     *
     * @param timestamp that will be converted. Must be a positive number.
     * @return {@link java.lang.String} with the date in seconds following the ISO 8601 conventions.
     */
    @NonNull
    public static String timestampToISOString(final long timestamp) {
        if (timestamp < 0) {
            throw new IllegalArgumentException(String.format("%s: timestampToISOString -> Received a negative timestamp", TAG));
        }
        return dateFormat.format(new Date(timestamp));
    }
}