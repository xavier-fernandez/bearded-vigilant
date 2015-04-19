package com.bearded.modules.sensor.internal.persistence;

import android.content.Context;
import android.hardware.Sensor;

import com.bearded.common.sensor.SensorType;
import com.bearded.modules.sensor.internal.domain.InternalSensorEntity;
import com.bearded.modules.sensor.internal.persistence.dao.DaoSession;

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
public class InternalSensorDatabaseFacade {

    private static final String DATABASE_NAME_SUFFIX = "internal-sensor-db";

    @NotNull
    private final InternalSensorEntityFacade mSensorEntityFacade;

    @NotNull
    private final DatabaseConnector mDatabaseHandler;

    @NotNull
    private final SensorType mSensorType;

    public InternalSensorDatabaseFacade(@NotNull final Context context,
                                        @NotNull final SensorType sensorType){
        final String databaseName = String.format("%s-%s", sensorType.getSensorTypeName(), DATABASE_NAME_SUFFIX);
        mDatabaseHandler = new DatabaseConnector(context, databaseName);
        mSensorType = sensorType;
        mSensorEntityFacade = new InternalSensorEntityFacade(sensorType);
    }

    /**
     * Writes in the database a sensor reading.
     * @return <code>true</code> if the reading was successful <code>false</code> otherwise.
     */
    public void insertReadingDatabase(@NotNull final Sensor sensor){

        final DaoSession session = mDatabaseHandler.getSession();
        final InternalSensorEntity sensorEntity = mSensorEntityFacade.getSensorEntity(session, sensor);

    }


}