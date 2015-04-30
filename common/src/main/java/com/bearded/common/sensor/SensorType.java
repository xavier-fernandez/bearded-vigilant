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
package com.bearded.common.sensor;

import android.hardware.Sensor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public enum SensorType {

    LIGHT(Sensor.TYPE_LIGHT, "Light Sensor", "Lux"),
    PROXIMITY(Sensor.TYPE_PROXIMITY, "Proximity Sensor", "cm"),
    AMBIENT_TEMPERATURE(Sensor.TYPE_AMBIENT_TEMPERATURE, "Ambient Temperature", "Celsius"),
    RELATIVE_HUMIDITY(Sensor.TYPE_RELATIVE_HUMIDITY, "Relative Humidity", "Relative Humidity Percentage");

    private final int mSensorId;
    @NonNull
    private final String mSensorTypeName;
    @NonNull
    private final String mSensorUnit;

    SensorType(final int sensorId, @NonNull final String sensorTypeName, @NonNull final String unit) {
        mSensorId = sensorId;
        mSensorTypeName = sensorTypeName;
        mSensorUnit = unit;
    }

    /**
     * Obtain a {@link SensorType} from a sensor ID.
     *
     * @param sensorId of an {@link android.hardware.Sensor}.
     * @return {@link SensorType} with the sensor Type with the id - <code>null</code> if not available.
     */
    @Nullable
    public static SensorType getSensorTypeFromId(final int sensorId) {
        for (final SensorType sensorType : values()) {
            if (sensorType.mSensorId == sensorId) {
                return sensorType;
            }
        }
        return null;
    }

    /**
     * Returns the sensor ID.
     *
     * @return {@link java.lang.Integer} with the sensor ID specified in {@link android.hardware.Sensor}
     */
    public int getSensorId() {
        return mSensorId;
    }

    /**
     * Returns the sensor type name in english.
     *
     * @return {@link java.lang.String} with the sensor type name. Cannot be null.
     */
    @NonNull
    public String getSensorTypeName() {
        return mSensorTypeName;
    }

    /**
     * Returns the sensor unit.
     *
     * @return {@link java.lang.String} with the sensor unit.
     */
    @NonNull
    public String getSensorUnit() {
        return mSensorUnit;
    }
}