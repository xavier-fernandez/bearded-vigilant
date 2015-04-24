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

package com.bearded.common.utils;

import android.annotation.TargetApi;
import android.hardware.Sensor;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.bearded.common.BuildConfig;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class SensorUtils {

    private static final String TAG = SensorUtils.class.getSimpleName();

    @TargetApi(21)
    @IntDef({Sensor.REPORTING_MODE_ONE_SHOT, Sensor.REPORTING_MODE_SPECIAL_TRIGGER,
            Sensor.REPORTING_MODE_ON_CHANGE, Sensor.REPORTING_MODE_CONTINUOUS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ReportingMode {}

    /**
     * This method obtains a reporting time string out of a reporting type.
     * This method should only be called from SDK version 21, since it uses the new sensor APIs
     * introduced in Android 5.
     * @param type {@link int} with the type.
     * @return {@link String} that correspond to the given {@param type}
     */
    @NonNull
    @TargetApi(21)
    public static String getReportingTimeString(@ReportingMode final int type) {
        switch (type) {
            case Sensor.REPORTING_MODE_ONE_SHOT:
                return "REPORTING_MODE_ONE_SHOT";
            case Sensor.REPORTING_MODE_SPECIAL_TRIGGER:
                return "REPORTING_MODE_SPECIAL_TRIGGER";
            case Sensor.REPORTING_MODE_ON_CHANGE:
                return "REPORTING_MODE_ON_CHANGE";
            case Sensor.REPORTING_MODE_CONTINUOUS:
                return "REPORTING_MODE_CONTINUOUS";
            default:
                throw new IllegalArgumentException(TAG + ": getReportingTimeString -> Type does" +
                        " not correspond to a valid reporting type");
        }
    }
}
