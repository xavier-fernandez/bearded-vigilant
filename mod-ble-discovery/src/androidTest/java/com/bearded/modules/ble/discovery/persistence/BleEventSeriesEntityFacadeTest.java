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

package com.bearded.modules.ble.discovery.persistence;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.bearded.modules.ble.discovery.domain.BleDeviceEntity;
import com.bearded.modules.ble.discovery.domain.BleEventSeriesEntity;
import com.bearded.modules.ble.discovery.persistence.dao.DaoSession;

public class BleEventSeriesEntityFacadeTest extends InstrumentationTestCase {

    private static final byte NUMBER_OF_SENSOR_ENTITIES = 5;

    private DatabaseConnector mDatabaseConnector;
    private BleEventSeriesEntityFacade mSeriesFacade;
    private BleDeviceEntity[] mSensorEntities;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mDatabaseConnector = new DatabaseConnector(getInstrumentation().getContext(), "TEST_DB");
        mSeriesFacade = new BleEventSeriesEntityFacade();
        generateSensorEntities();
    }

    private void generateSensorEntities() {
        mSensorEntities = new BleDeviceEntity[NUMBER_OF_SENSOR_ENTITIES];
        for (int i = 0; i < NUMBER_OF_SENSOR_ENTITIES; i++) {
            mSensorEntities[i] = new BleDeviceEntity();
            mSensorEntities[i].setDeviceAddress(String.format("AA:BB:CC:DD:EE:F%d", i));
            mSensorEntities[i].setAdvertiseName(String.format("Advertise Name %d", i));
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
     * Test the proper event series creation using a given device.
     *
     * @see BleEventSeriesEntityFacade#getActiveEventSeries(DaoSession, BleDeviceEntity)
     */
    @MediumTest
    public void testSensorEntityInsertion() {
        testPreConditions();
        final DaoSession session = mDatabaseConnector.getSession();
        final BleEventSeriesEntity series = mSeriesFacade.getActiveEventSeries(session, mSensorEntities[0]);
        assertNotNull(series);
        assertEquals(series.getBleDeviceEntity(), mSensorEntities[0]);
    }

    /**
     * Test when a sensor is inserted twice, only one single event series is created.
     *
     * @see BleEventSeriesEntityFacade#getActiveEventSeries(DaoSession, BleDeviceEntity)
     */
    @MediumTest
    public void testDuplicatedSensorEntityInsertion() {
        testPreConditions();
        final DaoSession session = mDatabaseConnector.getSession();
        final BleEventSeriesEntity series1 =
                mSeriesFacade.getActiveEventSeries(session, mSensorEntities[0]);
        assertNotNull(series1);
        final BleEventSeriesEntity series2 =
                mSeriesFacade.getActiveEventSeries(session, mSensorEntities[0]);
        assertNotNull(series2);
        assertEquals(series1, series2);
    }

    /**
     * Test the insertion of multiple different device entities.
     *
     * @see BleEventSeriesEntityFacade#getActiveEventSeries(DaoSession, BleDeviceEntity)
     */
    @MediumTest
    public void testDifferentSensorEntitiesInsertion() {
        testPreConditions();
        final DaoSession session = mDatabaseConnector.getSession();
        final BleEventSeriesEntity[] series = new BleEventSeriesEntity[NUMBER_OF_SENSOR_ENTITIES];
        for (int i = 0; i < NUMBER_OF_SENSOR_ENTITIES; i++) {
            series[i] = mSeriesFacade.getActiveEventSeries(session, mSensorEntities[i]);
            assertEquals(series[i].getBleDeviceEntity(), mSensorEntities[i]);
        }
    }

    /**
     * Test the insertion of multiple different device entities.
     *
     * @see BleEventSeriesEntityFacade#getActiveEventSeries(DaoSession, BleDeviceEntity)
     */
    @MediumTest
    public void testDuplicatedDifferentSensorEntitiesInsertion() {
        testPreConditions();
        final DaoSession session = mDatabaseConnector.getSession();
        final BleEventSeriesEntity[] series = new BleEventSeriesEntity[NUMBER_OF_SENSOR_ENTITIES];
        for (int i = 0; i < NUMBER_OF_SENSOR_ENTITIES; i++) {
            series[i] = mSeriesFacade.getActiveEventSeries(session, mSensorEntities[i]);
            assertEquals(series[i].getBleDeviceEntity(), mSensorEntities[i]);
        }
        // Retrieves an the inserted sensors again, and checks if the retrieved elements
        // are the same as in the first insertion.
        for (int i = 0; i < NUMBER_OF_SENSOR_ENTITIES; i++) {
            final BleEventSeriesEntity duplicatedSeries =
                    mSeriesFacade.getActiveEventSeries(session, mSensorEntities[i]);
            assertEquals(series[i], duplicatedSeries);
        }
    }

    /**
     * Test that all the closed elements from a device are retrieved properly.
     *
     * @see BleEventSeriesEntityFacade#getAllClosedEventSeriesFromDevice(DaoSession, BleDeviceEntity)
     */
    @MediumTest
    public void testGetAllClosedMeasurementSeriesFromSensor() {
        testPreConditions();
        final DaoSession session = mDatabaseConnector.getSession();
        //Test if the sensor facade is empty.
        assertEquals(mSeriesFacade.getAllClosedEventSeriesFromDevice(session, mSensorEntities[0]).size(), 0);
        for (int i = 0; i < NUMBER_OF_SENSOR_ENTITIES; i++) {
            mSeriesFacade.getActiveEventSeries(session, mSensorEntities[i]);
            assertEquals(0, mSeriesFacade.getAllClosedEventSeriesFromDevice(session, mSensorEntities[i]).size());
        }
        mSeriesFacade.updateAllEventSeriesEndTimestamp(session);
        for (int i = 0; i < NUMBER_OF_SENSOR_ENTITIES; i++) {
            assertEquals(1, mSeriesFacade.getAllClosedEventSeriesFromDevice(session, mSensorEntities[i]).size());
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