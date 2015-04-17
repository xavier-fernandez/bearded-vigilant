package com.bearded.common.sensor;

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
public enum SensorType {

    LIGHT (Sensor.TYPE_LIGHT, "Light Sensor");

    private final int mSensorId;
    @NotNull
    private final String mSensorTypeName;

    SensorType(final int sensorId, @NotNull final String sensorTypeName){
        mSensorId = sensorId;
        mSensorTypeName = sensorTypeName;
    }

    /**
     * Returns the sensor ID.
     * @return {@link java.lang.Integer} with the sensor ID specified in {@link android.hardware.Sensor}
     */
    public int getSensorId(){
        return mSensorId;
    }

    /**
     * Returns the sensor type name in english.
     * @return {@link java.lang.String} with the sensor type name. Cannot be null.
     */
    @NotNull
    public String getSensorTypeName(){
        return mSensorTypeName;
    }
}