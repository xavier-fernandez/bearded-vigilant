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
package com.bearded.modules.sensor.internal.persistence;

import android.content.Context;
import android.hardware.Sensor;

import com.bearded.common.sensor.SensorType;
import com.bearded.modules.sensor.internal.domain.InternalSensorEntity;
import com.bearded.modules.sensor.internal.domain.InternalSensorMeasurementSeriesEntity;
import com.bearded.modules.sensor.internal.persistence.dao.DaoMaster;
import com.bearded.modules.sensor.internal.persistence.dao.DaoSession;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

public class InternalSensorDatabaseFacade {

    private static final String DATABASE_NAME_SUFFIX = "internal-sensor-db";

    @NotNull
    private final InternalSensorEntityFacade mSensorEntityFacade;
    @NotNull
    private final InternalSensorMeasurementSeriesEntityFacade mMeasurementSeriesEntityFacade;
    @NotNull
    private final InternalSensorMeasurementEntityFacade mMeasurementEntityFacade;

    @NotNull
    private final DatabaseConnector mDatabaseHandler;

    public InternalSensorDatabaseFacade(@NotNull final Context context,
                                        @NotNull final SensorType sensorType) {
        final String databaseName = String.format("%s-%s", sensorType.getSensorTypeName(), DATABASE_NAME_SUFFIX);
        mDatabaseHandler = new DatabaseConnector(context, databaseName);
        mSensorEntityFacade = new InternalSensorEntityFacade(sensorType);
        mMeasurementSeriesEntityFacade = new InternalSensorMeasurementSeriesEntityFacade();
        mMeasurementEntityFacade = new InternalSensorMeasurementEntityFacade(sensorType);
    }

    /**
     * Writes in the database a sensor reading.
     */
    public void insertReadingDatabase(@NotNull final Sensor sensor,
                                      final float measurement,
                                      final int binSizeMilliseconds) {
        synchronized (mDatabaseHandler) {
            final DaoSession session = mDatabaseHandler.getSession();
            session.runInTx(new Runnable() {
                @Override
                public void run() {
                    final InternalSensorEntity sensorEntity = mSensorEntityFacade.getSensorEntity(session, sensor);
                    final InternalSensorMeasurementSeriesEntity series = mMeasurementSeriesEntityFacade.getMeasurementSeries(session, sensorEntity);
                    mMeasurementEntityFacade.addMeasurement(session, series, measurement, binSizeMilliseconds);
                }
            });
        }
    }

    /**
     * Removes all database content. Only used in application testing.
     */
    @TestOnly
    public void cleanDatabase() {
        synchronized (mDatabaseHandler) {
            DaoMaster.dropAllTables(mDatabaseHandler.getSession().getDatabase(), false);
            DaoMaster.createAllTables(mDatabaseHandler.getSession().getDatabase(), false);
        }
    }
}