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
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.bearded.modules.sensor.internal.domain.InternalSensorEntity;
import com.bearded.modules.sensor.internal.persistence.dao.DaoSession;

import static android.content.Context.SENSOR_SERVICE;
import static com.bearded.common.sensor.SensorType.LIGHT;

/**
 * Test the internal sensor entity facade using light sensors. We should consider that all modern
 * Android phones have a light sensor integrated, this test class only works with physical phone.
 * This is caused because it is forbidden to fake sensor values in Android, and the Sensor class
 * is final.
 */
public class InternalSensorEntityFacadeTest extends InstrumentationTestCase {

    protected DatabaseConnector mDatabaseConnector;
    protected InternalSensorEntityFacade mSensorFacade;
    protected SensorManager mSensorManager;
    protected Sensor mLightSensor;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mDatabaseConnector = new DatabaseConnector(getInstrumentation().getContext(), "TEST_DB");
        mDatabaseConnector.cleanDatabase();
        mSensorFacade = new InternalSensorEntityFacade(LIGHT);
        final Context context = getInstrumentation().getContext();
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        mLightSensor = mSensorManager.getDefaultSensor(LIGHT.getSensorId());
    }

    /**
     * Checks the conditions that all the class test are supposed to have before hand.
     */
    @SmallTest
    public void testPreConditions() {
        assertNotNull(mDatabaseConnector);
        assertNotNull(mSensorFacade);
        assertNotNull(mSensorManager);
        assertNotNull(mLightSensor);
        //Checks if the database is clean before the test starts.
        final DaoSession session = mDatabaseConnector.getSession();
        assertEquals(mSensorFacade.getAllSensorEntities(session).size(), 0);
    }

    @SmallTest
    public void testOneObjectInsertion() {
        testPreConditions();
        //Inserts a single sensor inside the database.
        final DaoSession session = mDatabaseConnector.getSession();
        final InternalSensorEntity sensorEntity = mSensorFacade.getSensorEntity(session, mLightSensor);
        assertNotNull(sensorEntity);
        //Checks that the returned object belongs to the same sensor as the inserted sensor.
        assertEquals(sensorEntity.getSensorName(), mLightSensor.getName());
        //Checks that only one object was inserted.
        assertEquals(mSensorFacade.getAllSensorEntities(session).size(), 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        //Cleans the database before the next test starts.
        if (mDatabaseConnector != null) {
            mDatabaseConnector.cleanDatabase();
        }
    }
}