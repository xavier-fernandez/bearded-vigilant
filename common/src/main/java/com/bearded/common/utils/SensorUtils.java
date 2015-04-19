package com.bearded.common.utils;

import android.hardware.Sensor;

import org.jetbrains.annotations.NotNull;

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
public abstract class SensorUtils {

    private static final String TAG = SensorUtils.class.getSimpleName();

    @NotNull
    public static String getReportingTimeString(final int type) {
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
