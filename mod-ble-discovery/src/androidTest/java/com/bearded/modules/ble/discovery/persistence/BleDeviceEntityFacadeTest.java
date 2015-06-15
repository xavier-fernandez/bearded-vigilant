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

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.bearded.modules.ble.discovery.domain.BleDeviceEntity;
import com.bearded.modules.ble.discovery.persistence.dao.DaoSession;

public class BleDeviceEntityFacadeTest extends InstrumentationTestCase {

    protected DatabaseConnector mDatabaseConnector;
    protected BleDeviceEntityFacade mBleDeviceFacade;

    /**
     * {@inheritDoc}
     */
    public void setUp() throws Exception {
        super.setUp();
        final Context context = getInstrumentation().getContext();
        mDatabaseConnector = new DatabaseConnector(context, "TestDatabase");
        mBleDeviceFacade = new BleDeviceEntityFacade();
    }

    /**
     * Checks the conditions that all the class test are supposed to have before hand.
     */
    @SmallTest
    public void testPreConditions() {
        assertNotNull(mDatabaseConnector);
        assertNotNull(mBleDeviceFacade);
        //Checks if the database is clean before the test starts.
        final DaoSession session = mDatabaseConnector.getSession();
        assertEquals(0, mBleDeviceFacade.getAllBleDevices(session).size());
    }

    /**
     * Test the following method, this test checks if the class works with one single insertion.
     *
     * @see BleDeviceEntityFacade#getBleDeviceEntity(DaoSession, String, String, Boolean, Boolean)
     */
    @MediumTest
    public void testOneObjectInsertion() {
        testPreConditions();
        final DaoSession session = mDatabaseConnector.getSession();
        //Inserts a single sensor inside the database.
        final String deviceAddress = "TEST BLE DEVICE";
        final String advertiseName = "TEST ADVERTISE NAME";
        final BleDeviceEntity bleDeviceEntity = mBleDeviceFacade.getBleDeviceEntity(session, deviceAddress, advertiseName, null, null);
        assertNotNull(bleDeviceEntity);
        //Checks that the returned object belongs to the same sensor as the inserted sensor.
        assertEquals(bleDeviceEntity.getDeviceAddress(), deviceAddress);
        assertEquals(bleDeviceEntity.getAdvertiseName(), advertiseName);
        //Checks that only one object was inserted.
        assertEquals(1, mBleDeviceFacade.getAllBleDevices(session).size());
    }

    /**
     * Test the following method, this test checks if the class works with multiple insertions of the same device.
     *
     * @see BleDeviceEntityFacade#getBleDeviceEntity(DaoSession, String, String, Boolean, Boolean)
     */
    @MediumTest
    public void testMultipleInsertionsSameBleDevice() {
        testPreConditions();
        //Insert a single sensor inside the database.
        final DaoSession session = mDatabaseConnector.getSession();
        final String deviceAddress = "TEST BLE DEVICE";
        final String advertiseName = "TEST ADVERTISE NAME";
        final BleDeviceEntity testDeviceEntity = mBleDeviceFacade.getBleDeviceEntity(session, deviceAddress, advertiseName, null, null);
        assertNotNull(testDeviceEntity);
        // Test multiple sensor insertion inside the database.
        for (int i = 0; i < 3; i++) {
            mBleDeviceFacade.getBleDeviceEntity(session, deviceAddress, advertiseName, null, null);
        }
        assertEquals(testDeviceEntity, mBleDeviceFacade.getBleDeviceEntity(session, deviceAddress, advertiseName, null, null));
        assertEquals(1, mBleDeviceFacade.getAllBleDevices(session).size());
    }

    /**
     * Test the following method, this test checks if the class works with multiple insertions.
     *
     * @see BleDeviceEntityFacade#getBleDeviceEntity(DaoSession, String, String, Boolean, Boolean)
     */
    @MediumTest
    public void testDifferentBleDevicesMultipleInsertions() {
        testPreConditions();
        //Insert a light sensor inside the database.
        final DaoSession session = mDatabaseConnector.getSession();
        final String deviceAddress = "TEST BLE DEVICE";
        final String advertiseName = "TEST ADVERTISE NAME";
        final BleDeviceEntity device1 = mBleDeviceFacade.getBleDeviceEntity(session, deviceAddress, advertiseName, null, null);
        assertNotNull(device1);
        //Insert a proximity sensor inside the database.
        final String deviceAddress2 = deviceAddress + "2";
        final String advertiseName2 = advertiseName + "2";
        final BleDeviceEntity device2 = mBleDeviceFacade.getBleDeviceEntity(session, deviceAddress2, advertiseName2, null, null);
        assertNotNull(device2);
        //Checks if two sensors are inside the database.
        assertEquals(2, mBleDeviceFacade.getAllBleDevices(session).size());
        assertTrue(mBleDeviceFacade.getAllBleDevices(session).contains(device1));
        assertTrue(mBleDeviceFacade.getAllBleDevices(session).contains(device2));
    }

    /**
     * {@inheritDoc}
     */
    public void tearDown() throws Exception {
        super.tearDown();
        //Cleans the database before the next test starts.
        if (mDatabaseConnector != null) {
            mDatabaseConnector.cleanDatabase();
        }
    }
}