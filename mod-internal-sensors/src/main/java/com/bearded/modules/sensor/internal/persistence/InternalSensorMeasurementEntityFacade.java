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

import com.bearded.common.utils.TimeUtils;
import com.bearded.modules.sensor.internal.domain.InternalSensorMeasurementEntity;
import com.bearded.modules.sensor.internal.domain.InternalSensorMeasurementSeriesEntity;
import com.bearded.modules.sensor.internal.persistence.dao.DaoSession;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class InternalSensorMeasurementEntityFacade {

    @NotNull
    private final Map<InternalSensorMeasurementSeriesEntity, SensorMeasurements> mSensorMeasurements;

    public InternalSensorMeasurementEntityFacade (){
        mSensorMeasurements = Collections.synchronizedMap(
                new HashMap<InternalSensorMeasurementSeriesEntity, SensorMeasurements>());
    }

    public void addMeasurement(@NotNull final DaoSession session,
                               @NotNull final InternalSensorMeasurementSeriesEntity series,
                               final float measurement, final int timeoutMillis) {
        synchronized (this) {
            SensorMeasurements measurements = mSensorMeasurements.get(series);
            if (measurements == null) {
                measurements = new SensorMeasurements();
            }
            measurements.addMeasurement(measurement);
            if (TimeUtils.millisecondsFromNow(measurements.firstElementTime) > timeoutMillis) {
                final InternalSensorMeasurementEntity measurementEntity = new InternalSensorMeasurementEntity();
                measurementEntity.setStartTimestamp(TimeUtils.timestampToISOString(measurements.firstElementTime));
                measurementEntity.setEndTimestamp(TimeUtils.timestampToISOString(DateTime.now()));
                measurementEntity.setBinSize(measurements.getBinSize());
                measurementEntity.setSensorValue(measurements.getMidSensorValue());
                session.getInternalSensorMeasurementEntityDao().insert(measurementEntity);
            }
        }
    }

    private static class SensorMeasurements {
        @NotNull
        private final List<Float> measurements = new ArrayList<>();
        @NotNull
        private final DateTime firstElementTime;

        private SensorMeasurements(){
            this.firstElementTime = DateTime.now();
        }

        private void addMeasurement(final float measurement) {
            this.measurements.add(measurement);
        }

        private float getMidSensorValue(){
            float sum = 0;
            for (final Float measurement : this.measurements) {
                sum += measurement;
            }
            return sum / this.measurements.size();
        }

        private short getBinSize() {
            return (short) this.measurements.size();
        }
    }
}