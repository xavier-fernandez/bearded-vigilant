package com.bearded.modules.sensor.internal.persistence;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.bearded.common.sensor.SensorType;
import com.bearded.modules.sensor.internal.domain.InternalSensorEntity;

import org.jetbrains.annotations.Nullable;

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
public class InternalSensorMeasurementSeriesEntityFacadeTest extends AbstractInternalSensorTestCase {

    @Nullable
    private InternalSensorEntity mTestInternalSensorEntity;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        assertNotNull(mDatabaseConnector);
        mTestInternalSensorEntity = new InternalSensorEntity();
        mTestInternalSensorEntity.setSensorName("testSensor");
        mTestInternalSensorEntity.setSensorType("LIGHT");
        mTestInternalSensorEntity.setSensorUnit("lux");
        mDatabaseConnector.getSession().insert(mTestInternalSensorEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void testPreConditions(){
        super.testPreConditions();
        assertNotNull(mTestInternalSensorEntity);
    }

    protected void gwinge(){
        assertNotNull("gmrie");
    }
}