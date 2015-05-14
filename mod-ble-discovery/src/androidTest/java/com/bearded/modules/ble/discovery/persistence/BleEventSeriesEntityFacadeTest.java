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

    private static final byte NUMBER_OF_DEVICE_ENTITIES = 5;

    private DatabaseConnector mDatabaseConnector;
    private BleEventSeriesEntityFacade mSeriesFacade;
    private BleDeviceEntity[] mDeviceEntities;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mDatabaseConnector = new DatabaseConnector(getInstrumentation().getContext(), "TEST_DB");
        mSeriesFacade = new BleEventSeriesEntityFacade();
        generateDeviceEntities();
    }

    private void generateDeviceEntities() {
        mDeviceEntities = new BleDeviceEntity[NUMBER_OF_DEVICE_ENTITIES];
        for (int i = 0; i < NUMBER_OF_DEVICE_ENTITIES; i++) {
            mDeviceEntities[i] = new BleDeviceEntity();
            mDeviceEntities[i].setDeviceAddress(String.format("AA:BB:CC:DD:EE:F%d", i));
            mDeviceEntities[i].setAdvertiseName(String.format("Advertise Name %d", i));
            mDatabaseConnector.getSession().insert(mDeviceEntities[i]);
        }
    }

    @SmallTest
    public void testPreConditions() {
        assertNotNull(mDatabaseConnector);
        assertNotNull(mSeriesFacade);
        assertNotNull(mDeviceEntities);
        assertEquals(NUMBER_OF_DEVICE_ENTITIES, mDeviceEntities.length);
        for (int i = 0; i < NUMBER_OF_DEVICE_ENTITIES; i++) {
            assertNotNull(mDeviceEntities[i]);
        }
    }

    /**
     * Test the proper event series creation using a given device.
     *
     * @see BleEventSeriesEntityFacade#getActiveEventSeries(DaoSession, BleDeviceEntity)
     */
    @MediumTest
    public void testDeviceEntityInsertion() {
        testPreConditions();
        final DaoSession session = mDatabaseConnector.getSession();
        final BleEventSeriesEntity series = mSeriesFacade.getActiveEventSeries(session, mDeviceEntities[0]);
        assertNotNull(series);
        assertEquals(series.getBleDeviceEntity(), mDeviceEntities[0]);
    }

    /**
     * Test when a sensor is inserted twice, only one single event series is created.
     *
     * @see BleEventSeriesEntityFacade#getActiveEventSeries(DaoSession, BleDeviceEntity)
     */
    @MediumTest
    public void testDuplicatedDeviceEntityInsertion() {
        testPreConditions();
        final DaoSession session = mDatabaseConnector.getSession();
        final BleEventSeriesEntity series1 =
                mSeriesFacade.getActiveEventSeries(session, mDeviceEntities[0]);
        assertNotNull(series1);
        final BleEventSeriesEntity series2 =
                mSeriesFacade.getActiveEventSeries(session, mDeviceEntities[0]);
        assertNotNull(series2);
        assertEquals(series1, series2);
    }

    /**
     * Test the insertion of multiple different device entities.
     *
     * @see BleEventSeriesEntityFacade#getActiveEventSeries(DaoSession, BleDeviceEntity)
     */
    @MediumTest
    public void testDifferentDeviceEntitiesInsertion() {
        testPreConditions();
        final DaoSession session = mDatabaseConnector.getSession();
        final BleEventSeriesEntity[] series = new BleEventSeriesEntity[NUMBER_OF_DEVICE_ENTITIES];
        for (int i = 0; i < NUMBER_OF_DEVICE_ENTITIES; i++) {
            series[i] = mSeriesFacade.getActiveEventSeries(session, mDeviceEntities[i]);
            assertEquals(series[i].getBleDeviceEntity(), mDeviceEntities[i]);
        }
    }

    /**
     * Test the insertion of multiple different device entities.
     *
     * @see BleEventSeriesEntityFacade#getActiveEventSeries(DaoSession, BleDeviceEntity)
     */
    @MediumTest
    public void testDuplicatedDifferentDeviceEntitiesInsertion() {
        testPreConditions();
        final DaoSession session = mDatabaseConnector.getSession();
        final BleEventSeriesEntity[] series = new BleEventSeriesEntity[NUMBER_OF_DEVICE_ENTITIES];
        for (int i = 0; i < NUMBER_OF_DEVICE_ENTITIES; i++) {
            series[i] = mSeriesFacade.getActiveEventSeries(session, mDeviceEntities[i]);
            assertEquals(series[i].getBleDeviceEntity(), mDeviceEntities[i]);
        }
        // Retrieves an the inserted sensors again, and checks if the retrieved elements
        // are the same as in the first insertion.
        for (int i = 0; i < NUMBER_OF_DEVICE_ENTITIES; i++) {
            final BleEventSeriesEntity duplicatedSeries =
                    mSeriesFacade.getActiveEventSeries(session, mDeviceEntities[i]);
            assertEquals(series[i], duplicatedSeries);
        }
    }

    /**
     * Test that all the closed elements from a device are retrieved properly.
     *
     * @see BleEventSeriesEntityFacade#getAllClosedEventSeriesFromDevice(DaoSession, BleDeviceEntity)
     */
    @MediumTest
    public void testGetAllClosedMeasurementSeriesFromDevice() {
        testPreConditions();
        final DaoSession session = mDatabaseConnector.getSession();
        //Test if the sensor facade is empty.
        assertEquals(mSeriesFacade.getAllClosedEventSeriesFromDevice(session, mDeviceEntities[0]).size(), 0);
        for (int i = 0; i < NUMBER_OF_DEVICE_ENTITIES; i++) {
            mSeriesFacade.getActiveEventSeries(session, mDeviceEntities[i]);
            assertEquals(0, mSeriesFacade.getAllClosedEventSeriesFromDevice(session, mDeviceEntities[i]).size());
        }
        mSeriesFacade.updateAllEventSeriesEndTimestamp(session);
        for (int i = 0; i < NUMBER_OF_DEVICE_ENTITIES; i++) {
            assertEquals(1, mSeriesFacade.getAllClosedEventSeriesFromDevice(session, mDeviceEntities[i]).size());
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