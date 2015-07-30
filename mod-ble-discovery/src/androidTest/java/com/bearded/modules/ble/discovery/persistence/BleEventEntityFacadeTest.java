package com.bearded.modules.ble.discovery.persistence;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.bearded.modules.ble.discovery.domain.BleDeviceEntity;
import com.bearded.modules.ble.discovery.domain.BleEventEntity;
import com.bearded.modules.ble.discovery.domain.BleEventSeriesEntity;
import com.bearded.modules.ble.discovery.persistence.dao.DaoSession;

import java.util.List;

public class BleEventEntityFacadeTest extends InstrumentationTestCase {

    private static final int MEASUREMENT_ENTITY_TIMEOUT = 250;

    private static final byte NUMBER_OF_MEASUREMENT_SERIES = 5;

    private DatabaseConnector mDatabaseConnector;
    private BleEventSeriesEntity[] mSeriesEntities;

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
        mSeriesEntities = new BleEventSeriesEntity[NUMBER_OF_MEASUREMENT_SERIES];
        final BleEventSeriesEntityFacade seriesFacade = new BleEventSeriesEntityFacade();
        for (int i = 0; i < NUMBER_OF_MEASUREMENT_SERIES; i++) {
            final BleDeviceEntity deviceEntity = new BleDeviceEntity();
            deviceEntity.setDeviceAddress(String.format("AA:BB:CC:DD:EE:F%d", i));
            deviceEntity.setAdvertiseName(String.format("Advertise Name %d", i));
            final DaoSession session = mDatabaseConnector.getSession();
            session.insert(deviceEntity);
            mSeriesEntities[i] = seriesFacade.getActiveEventSeries(session, deviceEntity);
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
        final BleEventEntityFacade measurementFacade = new BleEventEntityFacade(MEASUREMENT_ENTITY_TIMEOUT);
        assertEquals(0, measurementFacade.getAllEventsFromSeries(session, mSeriesEntities[0]).size());
    }

    /**
     * Test the following method for the insertion of one single measurement.
     *
     * @see BleEventEntityFacade#addMeasurement(DaoSession, BleEventSeriesEntity, byte)
     */
    public void testAddOneMeasurementSameBean() {
        final DaoSession session = mDatabaseConnector.getSession();
        final BleEventEntityFacade eventFacade = new BleEventEntityFacade(MEASUREMENT_ENTITY_TIMEOUT);
        // Adds a measurement
        final byte testValue = (byte) 30;
        eventFacade.addMeasurement(session, mSeriesEntities[0], testValue);
        final List<BleEventEntity> result = eventFacade.getAllEventsFromSeries(session, mSeriesEntities[0]);
        assertEquals(1, result.size());
        assertEquals(testValue, result.get(0).getMedianReceivedSignalStrength());
    }

    /**
     * Test the following method for the insertion of multiple events belonging to the same bean.
     *
     * @see BleEventEntityFacade#addMeasurement(DaoSession, BleEventSeriesEntity, byte)
     */
    public void testAddMultipleMeasurementsSameBean() {
        // Since the first value will always be inserted, we will insert and wait for testing other elements.
        final DaoSession session = mDatabaseConnector.getSession();
        final BleEventEntityFacade eventFacade = new BleEventEntityFacade(MEASUREMENT_ENTITY_TIMEOUT);
        // A value is inserted, being the first value the value will be written inside the database.
        final byte testValue = (byte) 30;
        eventFacade.addMeasurement(session, mSeriesEntities[0], testValue);
        eventFacade.storeAllOpenEvents(session);
        // Checks if the database is empty.
        final byte midValue = testValue + (byte) 21;
        final byte[] testValues = new byte[]{midValue - (byte) 1, midValue, midValue + (byte) 1};
        for (byte value : testValues) {
            eventFacade.addMeasurement(session, mSeriesEntities[0], value);
        }
        eventFacade.storeAllOpenEvents(session);
        // Obtains all the database elements.
        final List<BleEventEntity> result = eventFacade.getAllEventsFromSeries(session, mSeriesEntities[0]);
        // Checks if two results are available. (One measurement bean, and the tested bin)
        assertEquals(2, result.size());
        // Checks if the result order is correct.
        assertTrue(result.get(1).compareTo(result.get(0)) > 0);
        // Checks if the bin size is correct.
        assertEquals(testValues.length, result.get(1).getBinSize());
        // Check if the data wrapping is correct.
        assertEquals(midValue, result.get(1).getMedianReceivedSignalStrength());
    }

    /**
     * Test the following method for the insertion of one single event belonging to different series.
     *
     * @see BleEventEntityFacade#addMeasurement(DaoSession, BleEventSeriesEntity, byte)
     */
    public void testAddOneMeasurementDifferentBeans() {
        final DaoSession session = mDatabaseConnector.getSession();
        final BleEventEntityFacade measurementFacade = new BleEventEntityFacade(MEASUREMENT_ENTITY_TIMEOUT);
        // Adds a measurement
        final byte testValue = (byte) 50;
        for (final BleEventSeriesEntity series : mSeriesEntities) {
            measurementFacade.addMeasurement(session, series, testValue);
            measurementFacade.addMeasurement(session, series, testValue);
            final List<BleEventEntity> result = measurementFacade.getAllEventsFromSeries(session, series);
            assertEquals(1, result.size());
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