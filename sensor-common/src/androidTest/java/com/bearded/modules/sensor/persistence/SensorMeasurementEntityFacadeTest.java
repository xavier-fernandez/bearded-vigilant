package com.bearded.modules.sensor.persistence;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.bearded.modules.sensor.domain.SensorEntity;
import com.bearded.modules.sensor.domain.SensorMeasurementEntity;
import com.bearded.modules.sensor.domain.SensorMeasurementSeriesEntity;
import com.bearded.modules.sensor.persistence.dao.DaoSession;

import java.util.List;

public class SensorMeasurementEntityFacadeTest extends InstrumentationTestCase {

    private static final int MEASUREMENT_ENTITY_TIMEOUT = 250;

    private static final byte NUMBER_OF_MEASUREMENT_SERIES = 5;

    private DatabaseConnector mDatabaseConnector;
    private SensorMeasurementSeriesEntity[] mSeriesEntities;

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
        mSeriesEntities = new SensorMeasurementSeriesEntity[NUMBER_OF_MEASUREMENT_SERIES];
        final SensorMeasurementSeriesEntityFacade seriesFacade =
                new SensorMeasurementSeriesEntityFacade();
        for (int i = 0; i < NUMBER_OF_MEASUREMENT_SERIES; i++) {
            final SensorEntity sensorEntity = new SensorEntity();
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
        final SensorMeasurementEntityFacade measurementFacade =
                new SensorMeasurementEntityFacade(MEASUREMENT_ENTITY_TIMEOUT);
        assertEquals(0, measurementFacade.getAllMeasurementsFromSeries(session, mSeriesEntities[0]).size());
    }

    /**
     * Test the following method for the insertion of one single measurement.
     *
     * @see SensorMeasurementEntityFacade#addMeasurement(DaoSession, SensorMeasurementSeriesEntity, float)
     */
    public void testAddOneMeasurementSameBean() {
        final DaoSession session = mDatabaseConnector.getSession();
        final SensorMeasurementEntityFacade measurementFacade =
                new SensorMeasurementEntityFacade(MEASUREMENT_ENTITY_TIMEOUT);
        // Adds a measurement
        final float testValue = 30f;
        measurementFacade.addMeasurement(session, mSeriesEntities[0], testValue);
        final List<SensorMeasurementEntity> result =
                measurementFacade.getAllMeasurementsFromSeries(session, mSeriesEntities[0]);
        assertEquals(1, result.size());
        assertEquals(testValue, result.get(0).getMedianSensorValue());
    }

    /**
     * Test the following method for the insertion of multiple measurements belonging to the same bean.
     *
     * @see SensorMeasurementEntityFacade#addMeasurement(DaoSession, SensorMeasurementSeriesEntity, float)
     */
    public void testAddMultipleMeasurementsSameBean() {
        // Since the first value will always be inserted, we will insert and wait for testing other elements.
        final DaoSession session = mDatabaseConnector.getSession();
        final SensorMeasurementEntityFacade measurementFacade =
                new SensorMeasurementEntityFacade(MEASUREMENT_ENTITY_TIMEOUT);
        // A value is inserted, being the first value the value will be written inside the database.
        final float testValue = 30f;
        measurementFacade.addMeasurement(session, mSeriesEntities[0], testValue);
        measurementFacade.storeAllOpenMeasurements(session);
        // Checks if the database is empty.
        final float midValue = testValue + 21f;
        final float[] testValues = new float[]{midValue - 1, midValue, midValue + 1};
        for (float value : testValues) {
            measurementFacade.addMeasurement(session, mSeriesEntities[0], value);
        }
        measurementFacade.storeAllOpenMeasurements(session);
        // Obtains all the database elements.
        final List<SensorMeasurementEntity> result =
                measurementFacade.getAllMeasurementsFromSeries(session, mSeriesEntities[0]);
        // Checks if two results are available. (One measurement bean, and the tested bin)
        assertEquals(2, result.size());
        // Checks if the result order is correct.
        assertTrue(result.get(1).compareTo(result.get(0)) > 0);
        // Checks if the bin size is correct.
        assertEquals(testValues.length, result.get(1).getBinSize());
        // Check if the data wrapping is correct.
        assertEquals(midValue, result.get(1).getMedianSensorValue());
    }

    /**
     * Test the following method for the insertion of one single measurement belonging to different series.
     *
     * @see SensorMeasurementEntityFacade#addMeasurement(DaoSession, SensorMeasurementSeriesEntity, float)
     */
    public void testAddOneMeasurementDifferentBeans() {
        final DaoSession session = mDatabaseConnector.getSession();
        final SensorMeasurementEntityFacade measurementFacade =
                new SensorMeasurementEntityFacade(MEASUREMENT_ENTITY_TIMEOUT);
        // Adds a measurement
        final float testValue = 50f;
        for (final SensorMeasurementSeriesEntity mSeriesEntity : mSeriesEntities) {
            measurementFacade.addMeasurement(session, mSeriesEntity, testValue);
            final List<SensorMeasurementEntity> result =
                    measurementFacade.getAllMeasurementsFromSeries(session, mSeriesEntity);
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