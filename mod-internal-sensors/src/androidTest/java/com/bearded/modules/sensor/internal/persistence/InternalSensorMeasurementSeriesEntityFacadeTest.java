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
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.bearded.modules.sensor.internal.domain.InternalSensorEntity;
import com.bearded.modules.sensor.internal.domain.InternalSensorMeasurementSeriesEntity;
import com.bearded.modules.sensor.internal.persistence.dao.DaoSession;

public class InternalSensorMeasurementSeriesEntityFacadeTest extends InstrumentationTestCase {

    private static final byte NUMBER_OF_SENSOR_ENTITIES = 5;

    private DatabaseConnector mDatabaseConnector;
    private InternalSensorMeasurementSeriesEntityFacade mSeriesFacade;
    private InternalSensorEntity[] mSensorEntities;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mDatabaseConnector = new DatabaseConnector(getInstrumentation().getContext(), "TEST_DB");
        mSeriesFacade = new InternalSensorMeasurementSeriesEntityFacade();
        generateSensorEntities();
    }

    private void generateSensorEntities() {
        mSensorEntities = new InternalSensorEntity[NUMBER_OF_SENSOR_ENTITIES];
        for (int i = 0; i < NUMBER_OF_SENSOR_ENTITIES; i++) {
            mSensorEntities[i] = new InternalSensorEntity();
            mSensorEntities[i].setSensorName(String.format("test sensor %d", i));
            mSensorEntities[i].setSensorType(String.format("test type %d", i));
            mSensorEntities[i].setSensorUnit(String.format("unit %d", i));
            mDatabaseConnector.getSession().insert(mSensorEntities[i]);
        }
    }

    @SmallTest
    public void testPreConditions() {
        assertNotNull(mDatabaseConnector);
        assertNotNull(mSeriesFacade);
        assertNotNull(mSensorEntities);
        assertEquals(NUMBER_OF_SENSOR_ENTITIES, mSensorEntities.length);
        for (int i = 0; i < NUMBER_OF_SENSOR_ENTITIES; i++) {
            assertNotNull(mSensorEntities[i]);
        }
    }

    /**
     * Test the proper measurement series creation using a given sensor.
     *
     * @see InternalSensorMeasurementSeriesEntityFacade#getActiveMeasurementSeries(DaoSession, InternalSensorEntity)
     */
    @MediumTest
    public void testSensorEntityInsertion() {
        testPreConditions();
        final DaoSession session = mDatabaseConnector.getSession();
        final InternalSensorMeasurementSeriesEntity series =
                mSeriesFacade.getActiveMeasurementSeries(session, mSensorEntities[0]);
        assertNotNull(series);
        assertEquals(series.getInternalSensorEntity(), mSensorEntities[0]);
    }

    /**
     * Test when a sensor is inserted twice, only one single measurement series is created.
     *
     * @see InternalSensorMeasurementSeriesEntityFacade#getActiveMeasurementSeries(DaoSession, InternalSensorEntity)
     */
    @MediumTest
    public void testDuplicatedSensorEntityInsertion() {
        testPreConditions();
        final DaoSession session = mDatabaseConnector.getSession();
        final InternalSensorMeasurementSeriesEntity series1 =
                mSeriesFacade.getActiveMeasurementSeries(session, mSensorEntities[0]);
        assertNotNull(series1);
        final InternalSensorMeasurementSeriesEntity series2 =
                mSeriesFacade.getActiveMeasurementSeries(session, mSensorEntities[0]);
        assertNotNull(series2);
        assertEquals(series1, series2);
    }

    /**
     * Test the insertion of multiple different sensor entities.
     *
     * @see InternalSensorMeasurementSeriesEntityFacade#getActiveMeasurementSeries(DaoSession, InternalSensorEntity)
     */
    @MediumTest
    public void testDifferentSensorEntitiesInsertion() {
        testPreConditions();
        final DaoSession session = mDatabaseConnector.getSession();
        final InternalSensorMeasurementSeriesEntity[] series =
                new InternalSensorMeasurementSeriesEntity[NUMBER_OF_SENSOR_ENTITIES];
        for (int i = 0; i < NUMBER_OF_SENSOR_ENTITIES; i++) {
            series[i] = mSeriesFacade.getActiveMeasurementSeries(session, mSensorEntities[i]);
            assertEquals(series[i].getInternalSensorEntity(), mSensorEntities[i]);
        }
    }

    /**
     * Test the insertion of multiple different sensor entities.
     *
     * @see InternalSensorMeasurementSeriesEntityFacade#getActiveMeasurementSeries(DaoSession, InternalSensorEntity)
     */
    @MediumTest
    public void testDuplicatedDifferentSensorEntitiesInsertion() {
        testPreConditions();
        final DaoSession session = mDatabaseConnector.getSession();
        final InternalSensorMeasurementSeriesEntity[] series =
                new InternalSensorMeasurementSeriesEntity[NUMBER_OF_SENSOR_ENTITIES];
        for (int i = 0; i < NUMBER_OF_SENSOR_ENTITIES; i++) {
            series[i] = mSeriesFacade.getActiveMeasurementSeries(session, mSensorEntities[i]);
            assertEquals(series[i].getInternalSensorEntity(), mSensorEntities[i]);
        }
        // Retrieves an the inserted sensors again, and checks if the retrieved elements
        // are the same as in the first insertion.
        for (int i = 0; i < NUMBER_OF_SENSOR_ENTITIES; i++) {
            final InternalSensorMeasurementSeriesEntity duplicatedSeries =
                    mSeriesFacade.getActiveMeasurementSeries(session, mSensorEntities[i]);
            assertEquals(series[i], duplicatedSeries);
        }
    }

    /**
     * Test that all the closed elements from a sensor are retrieved properly.
     *
     * @see InternalSensorMeasurementSeriesEntityFacade#getAllClosedMeasurementSeriesFromSensor(DaoSession, InternalSensorEntity)
     */
    @MediumTest
    public void testGetAllClosedMeasurementSeriesFromSensor() {
        testPreConditions();
        final DaoSession session = mDatabaseConnector.getSession();
        //Test if the sensor facade is empty.
        assertEquals(mSeriesFacade.getAllClosedMeasurementSeriesFromSensor(session, mSensorEntities[0]).size(), 0);
        for (int i = 0; i < NUMBER_OF_SENSOR_ENTITIES; i++) {
            mSeriesFacade.getActiveMeasurementSeries(session, mSensorEntities[i]);
            assertEquals(0, mSeriesFacade.getAllClosedMeasurementSeriesFromSensor(session, mSensorEntities[i]).size());
        }
        mSeriesFacade.updateAllMeasurementSeriesEndTimestamp(session);
        for (int i = 0; i < NUMBER_OF_SENSOR_ENTITIES; i++) {
            assertEquals(1, mSeriesFacade.getAllClosedMeasurementSeriesFromSensor(session, mSensorEntities[i]).size());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (mDatabaseConnector != null) {
            mDatabaseConnector.cleanDatabase();
        }
    }
}