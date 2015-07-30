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
package com.bearded.modules.sensor.persistence;

import android.annotation.TargetApi;
import android.hardware.Sensor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bearded.common.annotation.ReportingMode;
import com.bearded.common.sensor.SensorType;
import com.bearded.common.sensor.SensorUtils;
import com.bearded.modules.sensor.domain.SensorEntity;
import com.bearded.modules.sensor.persistence.dao.DaoSession;
import com.bearded.modules.sensor.persistence.dao.SensorEntityDao;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;

import static com.bearded.common.sensor.SensorType.getSensorTypeFromId;

class SensorEntityFacade {

    private static final String TAG = SensorEntityFacade.class.getSimpleName();

    @NonNull
    private final Map<String, SensorEntity> mKnownSensors;

    SensorEntityFacade() {
        mKnownSensors = Collections.synchronizedMap(new HashMap<String, SensorEntity>());
    }

    /**
     * Obtains a sensor entity, if it is available, from the database.
     * Creates and inserts a sensor entity in the database, if it is not available.
     *
     * @param session for obtaining or creating a {@link SensorEntity} from the database.
     * @param sensor  that wants to be retrieved from the database.
     * @return {@link SensorEntity} of the sensor.
     */
    @NonNull
    SensorEntity getSensorEntity(@NonNull DaoSession session,
                                 @NonNull Sensor sensor) {
        if (mKnownSensors.containsKey(sensor.getName())) {
            return mKnownSensors.get(sensor.getName());
        }
        final SensorEntityDao dao = session.getSensorEntityDao();
        final QueryBuilder<SensorEntity> queryBuilder = dao.queryBuilder();
        queryBuilder.where(SensorEntityDao.Properties.SensorName.eq(sensor.getName()));
        final String sensorTypeName = getSensorTypeFromId(sensor.getType()).getSensorTypeName();
        queryBuilder.where(SensorEntityDao.Properties.SensorType.eq(sensorTypeName));
        final List<SensorEntity> sensorEntityList = queryBuilder.list();
        SensorEntity sensorEntity = null;
        try {
            if (sensorEntityList.isEmpty()) {
                sensorEntity = insertSensor(session, sensor);
            } else {
                sensorEntity = sensorEntityList.get(0);
            }
            return sensorEntity;
        } finally {
            mKnownSensors.put(sensor.getName(), sensorEntity);
        }
    }

    /**
     * Obtains a sensor entity, if it is available, from the database.
     * Creates and inserts a sensor entity in the database, if it is not available.
     *
     * @param session       for obtaining or creating a {@link SensorEntity} from the database.
     * @param sensorAddress that wants to be retrieved from the database.
     * @param sensorType    of the sensor.
     * @param sensorUnit    of the sensor.
     * @param sensorName    of the inserted sensor.
     * @return {@link SensorEntity} of the sensor.
     */
    @NonNull
    SensorEntity getSensorEntity(@NonNull DaoSession session,
                                 @NonNull String sensorAddress,
                                 @NonNull SensorType sensorType,
                                 @NonNull String sensorUnit,
                                 @NonNull String sensorName) {
        if (mKnownSensors.containsKey(sensorAddress)) {
            return mKnownSensors.get(sensorAddress);
        }
        final SensorEntityDao dao = session.getSensorEntityDao();
        final QueryBuilder<SensorEntity> queryBuilder = dao.queryBuilder();
        queryBuilder.where(SensorEntityDao.Properties.SensorAddress.eq(sensorAddress));
        queryBuilder.where(SensorEntityDao.Properties.SensorType.eq(sensorType.getSensorTypeName()));
        final List<SensorEntity> sensorEntityList = queryBuilder.list();
        SensorEntity sensorEntity = null;
        try {
            if (sensorEntityList.isEmpty()) {
                sensorEntity = new SensorEntity();
                sensorEntity.setSensorAddress(sensorAddress);
                sensorEntity.setSensorType(sensorType.getSensorTypeName());
                sensorEntity.setSensorUnit(sensorUnit);
                sensorEntity.setSensorName(sensorName);
                session.insert(sensorEntity);
                return sensorEntity;
            } else {
                sensorEntity = sensorEntityList.get(0);
                return sensorEntity;
            }
        } finally {
            mKnownSensors.put(sensorAddress, sensorEntity);
        }
    }

    /**
     * Obtains all sensor entities stored in the database.
     *
     * @param session needed to retrieve all sensors from the database.
     * @return {@link List} with all {@link SensorEntity}
     */
    @NonNull
    List<SensorEntity> getAllSensorEntities(@NonNull DaoSession session) {
        final SensorEntityDao dao = session.getSensorEntityDao();
        final QueryBuilder<SensorEntity> queryBuilder = dao.queryBuilder();
        return queryBuilder.listLazy();
    }

    /**
     * Inserts a sensor, and all the obtainable information inside the database.
     *
     * @param sensor that will be inserted inside the database.
     * @return {@link SensorEntity} of the sensor.
     */
    @NonNull
    @TargetApi(22)
    private SensorEntity insertSensor(@NonNull DaoSession session,
                                      @NonNull Sensor sensor) {
        final SensorEntity sensorEntity = new SensorEntity();
        sensorEntity.setSensorName(sensor.getName());
        final SensorType sensorType = getSensorTypeFromId(sensor.getType());
        sensorEntity.setSensorType(sensorType.getSensorTypeName());
        sensorEntity.setSensorUnit(sensorType.getSensorUnit());
        sensorEntity.setMinimumDelayMicroseconds(sensor.getMinDelay());
        if (Build.VERSION.SDK_INT >= 21) {
            // getMaxDelay is only available in SDK 21+
            sensorEntity.setMaximumDelayMicroseconds(sensor.getMaxDelay());
            // getReportingMode is only available in SDK 21+
            @ReportingMode
            final int reportingMode = sensor.getReportingMode();
            sensorEntity.setReportingMode(SensorUtils.getReportingTimeString(reportingMode));
        }
        if (Build.VERSION.SDK_INT >= 19) {
            sensorEntity.setFifoMaxEventCount(sensor.getFifoMaxEventCount());
            sensorEntity.setFifoReservedEventCount(sensor.getFifoReservedEventCount());
        }
        sensorEntity.setMaximumRange(sensor.getMaximumRange());
        sensorEntity.setPowerInMilliAmperes(sensor.getPower());
        sensorEntity.setSensorResolution(sensor.getResolution());
        sensorEntity.setSensorVendor(sensor.getVendor());
        sensorEntity.setSensorVersion(sensor.getVersion());
        session.insert(sensorEntity);
        Log.d(TAG, "insertSensor -> Inserted sensor -> " + sensorEntity.toJsonObject().toString());
        return sensorEntity;
    }
}