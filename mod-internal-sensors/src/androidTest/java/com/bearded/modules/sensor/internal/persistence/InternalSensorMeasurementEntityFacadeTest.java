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

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.bearded.modules.sensor.internal.domain.InternalSensorEntity;
import com.bearded.modules.sensor.internal.domain.InternalSensorMeasurementEntity;
import com.bearded.modules.sensor.internal.domain.InternalSensorMeasurementSeriesEntity;
import com.bearded.modules.sensor.internal.persistence.dao.DaoSession;

import java.util.List;

public class InternalSensorMeasurementEntityFacadeTest extends InstrumentationTestCase {

    private static final int MEASUREMENT_ENTITY_TIMEOUT = 250;

    private static final byte NUMBER_OF_MEASUREMENT_SERIES = 5;

    private DatabaseConnector mDatabaseConnector;
    private InternalSensorMeasurementSeriesEntity[] mSeriesEntities;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mDatabaseConnector = new DatabaseConnector(getInstrumentation().getContext(), "TEST_DB");
        generateMeasurementSeriesEntities();
    }

    private void generateMeasurementSeriesEntities() {
        mSeriesEntities = new InternalSensorMeasurementSeriesEntity[NUMBER_OF_MEASUREMENT_SERIES];
        final InternalSensorMeasurementSeriesEntityFacade seriesFacade =
                new InternalSensorMeasurementSeriesEntityFacade();
        for (int i = 0; i < NUMBER_OF_MEASUREMENT_SERIES; i++) {
            final InternalSensorEntity sensorEntity = new InternalSensorEntity();
            sensorEntity.setSensorName(String.format("test sensor %d", i));
            sensorEntity.setSensorType(String.format("test type %d", i));
            sensorEntity.setSensorUnit(String.format("unit %d", i));
            final DaoSession session = mDatabaseConnector.getSession();
            session.insert(sensorEntity);
            mSeriesEntities[i] = seriesFacade.getActiveMeasurementSeries(session, sensorEntity);
        }
    }

    @SmallTest
    public void testPreConditions() {
        assertNotNull(mDatabaseConnector);
        assertEquals(NUMBER_OF_MEASUREMENT_SERIES, mSeriesEntities.length);
        for (int i = 0; i < NUMBER_OF_MEASUREMENT_SERIES; i++) {
            assertNotNull(mSeriesEntities[i]);
        }
        final DaoSession session = mDatabaseConnector.getSession();
        //Checks if the database is empty.
        new InternalSensorMeasurementEntityFacade(MEASUREMENT_ENTITY_TIMEOUT);
        final InternalSensorMeasurementEntityFacade measurementFacade =
                new InternalSensorMeasurementEntityFacade(MEASUREMENT_ENTITY_TIMEOUT);
        assertEquals(0, measurementFacade.obtainAllMeasurementsFromSeries(session, mSeriesEntities[0]).size());
    }

    /**
     * Test the following method for the insertion of one single measurement.
     *
     * @see InternalSensorMeasurementEntityFacade#addMeasurement(DaoSession, InternalSensorMeasurementSeriesEntity, float)
     */
    public void testAddOneMeasurementSameBean() {
        final DaoSession session = mDatabaseConnector.getSession();
        final InternalSensorMeasurementEntityFacade measurementFacade =
                new InternalSensorMeasurementEntityFacade(MEASUREMENT_ENTITY_TIMEOUT);
        // Adds a measurement
        final float testValue = 50f;
        measurementFacade.addMeasurement(session, mSeriesEntities[0], testValue);
        final List<InternalSensorMeasurementEntity> result =
                measurementFacade.obtainAllMeasurementsFromSeries(session, mSeriesEntities[0]);
        assertEquals(1, result.size());
        assertEquals(testValue, result.get(0).getSensorValue());
    }

    /**
     * Test the following method for the insertion of multiple measurements belonging to the same bean.
     *
     * @see InternalSensorMeasurementEntityFacade#addMeasurement(DaoSession, InternalSensorMeasurementSeriesEntity, float)
     */
    public void testAddMultipleMeasurementsSameBean() {
        final DaoSession session = mDatabaseConnector.getSession();
        final InternalSensorMeasurementEntityFacade measurementFacade =
                new InternalSensorMeasurementEntityFacade(MEASUREMENT_ENTITY_TIMEOUT);
        //Checks if the database is empty.
        final float midValue = 51f;
        final float[] testValues = new float[]{midValue - 1, midValue, midValue + 1};
        for (float testValue : testValues) {
            measurementFacade.addMeasurement(session, mSeriesEntities[0], testValue);
        }
        final List<InternalSensorMeasurementEntity> result =
                measurementFacade.obtainAllMeasurementsFromSeries(session, mSeriesEntities[0]);
        assertEquals(1, result.size());
        assertEquals(midValue, result.get(0).getSensorValue());
    }

    /**
     * Test the following method for the insertion of one single measurement belonging to different beans.
     *
     * @see InternalSensorMeasurementEntityFacade#addMeasurement(DaoSession, InternalSensorMeasurementSeriesEntity, float)
     */
    public void testAddOneMeasurementDifferentBeans() {
        final DaoSession session = mDatabaseConnector.getSession();
        final InternalSensorMeasurementEntityFacade measurementFacade =
                new InternalSensorMeasurementEntityFacade(MEASUREMENT_ENTITY_TIMEOUT);
        // Adds a measurement
        final float testValue = 50f;
        measurementFacade.addMeasurement(session, mSeriesEntities[1], testValue);
        while (true) {
            try {
                Thread.sleep(MEASUREMENT_ENTITY_TIMEOUT + 1);
                break;
            } catch (final InterruptedException ignored) {
            }
        }
        for (int i = 0; i < 3; i++) {
            final List<InternalSensorMeasurementEntity> result =
                    measurementFacade.obtainAllMeasurementsFromSeries(session, mSeriesEntities[0]);
            assertEquals(i + 1, result.size());
        }
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