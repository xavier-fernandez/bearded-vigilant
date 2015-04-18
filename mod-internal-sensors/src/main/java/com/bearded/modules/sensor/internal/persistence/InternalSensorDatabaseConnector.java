package com.bearded.modules.sensor.internal.persistence;

import android.content.Context;
import android.hardware.Sensor;
import android.os.Build;

import com.bearded.common.sensor.SensorType;
import com.bearded.common.utils.SensorUtils;
import com.bearded.modules.sensor.internal.domain.InternalSensorEntity;
import com.bearded.modules.sensor.internal.persistence.dao.DaoSession;
import com.bearded.modules.sensor.internal.persistence.dao.InternalSensorEntityDao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;

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
public class InternalSensorDatabaseConnector {

    private static final String DATABASE_NAME_SUFFIX = "internal-sensor-db";

    @NotNull
    private final InternalSensorDatabaseHandler mDatabaseHandler;

    @NotNull
    private final Map<String, InternalSensorEntity> mKnownSensors;

    @NotNull
    private final SensorType mSensorType;

    public InternalSensorDatabaseConnector(@NotNull final Context context,
                                           @NotNull final SensorType mSensorType){
        final String databaseName = String.format("%s-%s", mSensorType.getSensorTypeName(), DATABASE_NAME_SUFFIX);
        mDatabaseHandler = new InternalSensorDatabaseHandler(context, databaseName);
        mKnownSensors = Collections.synchronizedMap(new HashMap<String, InternalSensorEntity>());
        this.mSensorType = mSensorType;
    }

    /**
     * Writes in the database a sensor reading.
     * @return <code>true</code> if the reading was successful <code>false</code> otherwise.
     */
    public void insertReadingDatabase(@NotNull final Sensor sensor){

        final DaoSession session = mDatabaseHandler.getSession();
        final InternalSensorEntity sensorEntity = getSensorEntity(sensor);

    }

    /**
     * Obtains a sensor entity, if it is available, from the database.
     * @param sensor that wants to be retrieved from the database.
     * @return {@link InternalSensorEntity} of the sensor.
     */
    @Nullable
    private InternalSensorEntity getSensorEntity(@NotNull final Sensor sensor){
        if (mKnownSensors.containsKey(sensor.getName())) {
            return mKnownSensors.get(sensor.getName());
        }
        final DaoSession session = mDatabaseHandler.getSession();
        final InternalSensorEntityDao dao = session.getInternalSensorEntityDao();
        final QueryBuilder<InternalSensorEntity> queryBuilder = dao.queryBuilder();
        queryBuilder.where(InternalSensorEntityDao.Properties.SensorName.eq(sensor.getName()));
        final List<InternalSensorEntity> internalSensorEntityList = queryBuilder.list();
        if (internalSensorEntityList.isEmpty()) {
            return insertSensor(sensor);
        }
        final InternalSensorEntity sensorEntity = internalSensorEntityList.get(0);
        mKnownSensors.put(sensor.getName(), sensorEntity);
        return sensorEntity;
    }

    /**
     * Inserts a sensor, and all the obtainable information inside the database.
     * @param sensor that will be inserted inside the database.
     * @return {@link InternalSensorEntity} of the sensor.
     */
    @NotNull
    private InternalSensorEntity insertSensor(@NotNull final Sensor sensor){
        final InternalSensorEntity sensorEntity = new InternalSensorEntity();
        sensorEntity.setSensorName(sensor.getName());
        sensorEntity.setSensorType(mSensorType.getSensorTypeName());
        sensorEntity.setSensorUnit(mSensorType.getSensorUnit());
        sensorEntity.setMinimumDelayMicroseconds(sensor.getMinDelay());
        if (Build.VERSION.SDK_INT >= 21) {
            sensorEntity.setMaximumDelayMicroseconds(sensor.getMaxDelay());
        }
        sensorEntity.setFifoMaxEventCount(sensor.getFifoMaxEventCount());
        sensorEntity.setFifoReservedEventCount(sensor.getFifoReservedEventCount());
        sensorEntity.setMaximumRange(sensor.getMaximumRange());
        if (Build.VERSION.SDK_INT >= 21) {
            sensorEntity.setReportingMode(SensorUtils.getReportingTimeString(sensor.getReportingMode()));
        }
        sensorEntity.setPowerInMilliAmperes(sensor.getPower());
        sensorEntity.setSensorResolution(sensor.getResolution());
        sensorEntity.setSensorVendor(sensor.getVendor());
        sensorEntity.setSensorVersion(sensor.getVersion());
        final DaoSession session = mDatabaseHandler.getSession();
        session.getInternalSensorEntityDao().insert(sensorEntity);
        return sensorEntity;
    }
}