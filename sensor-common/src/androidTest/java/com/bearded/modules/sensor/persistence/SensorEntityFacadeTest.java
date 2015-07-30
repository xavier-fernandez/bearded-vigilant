package com.bearded.modules.sensor.persistence;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.bearded.modules.sensor.domain.SensorEntity;
import com.bearded.modules.sensor.persistence.dao.DaoSession;

import static android.content.Context.SENSOR_SERVICE;
import static com.bearded.common.sensor.SensorType.LIGHT;
import static com.bearded.common.sensor.SensorType.PROXIMITY;

/**
 * Test the internal sensor entity facade using light sensors. We should consider that all modern
 * Android phones have a light and a proximity sensor integrated, this test class only works with
 * physical phones. This is caused because it is forbidden to fake sensor values in Android, and
 * the Sensor class is final, so we are not allowed to mock the sensor objects.
 */
public class SensorEntityFacadeTest extends InstrumentationTestCase {

    protected DatabaseConnector mDatabaseConnector;
    protected SensorEntityFacade mSensorFacade;
    protected SensorManager mSensorManager;
    protected Sensor mLightSensor;
    protected Sensor mProximitySensor;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mDatabaseConnector = new DatabaseConnector(getInstrumentation().getContext(), "TEST_DB");
        mDatabaseConnector.cleanDatabase();
        mSensorFacade = new SensorEntityFacade();
        final Context context = getInstrumentation().getContext();
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        mLightSensor = mSensorManager.getDefaultSensor(LIGHT.getSensorId());
        mProximitySensor = mSensorManager.getDefaultSensor(PROXIMITY.getSensorId());
    }

    /**
     * Checks the conditions that all the class test are supposed to have before each test
     * method is executed.
     */
    @SmallTest
    public void testPreConditions() {
        assertNotNull(mDatabaseConnector);
        assertNotNull(mSensorFacade);
        assertNotNull(mSensorManager);
        assertNotNull(mLightSensor);
        //Checks if the database is clean before the test starts.
        final DaoSession session = mDatabaseConnector.getSession();
        assertEquals(0, mSensorFacade.getAllSensorEntities(session).size());
    }

    /**
     * Test the insertion of one single {@link SensorEntity} inside the database.
     *
     * @see SensorEntityFacade#getSensorEntity(DaoSession, Sensor)
     */
    @MediumTest
    public void testOneObjectInsertion() {
        testPreConditions();
        //Inserts a single sensor inside the database.
        final DaoSession session = mDatabaseConnector.getSession();
        final SensorEntity sensorEntity = mSensorFacade.getSensorEntity(session, mLightSensor);
        assertNotNull(sensorEntity);
        //Checks that the returned object belongs to the same sensor as the inserted sensor.
        assertEquals(sensorEntity.getSensorName(), mLightSensor.getName());
        //Checks that only one object was inserted.
        assertEquals(1, mSensorFacade.getAllSensorEntities(session).size());
    }

    /**
     * Test the insertion of one single {@link SensorEntity} inside the database, asking
     * for it several times. (Only one {@link SensorEntity} is created.
     *
     * @see SensorEntityFacade#getSensorEntity(DaoSession, Sensor)
     */
    @MediumTest
    public void testMultipleInsertionsSameSensor() {
        testPreConditions();
        //Insert a single sensor inside the database.
        final DaoSession session = mDatabaseConnector.getSession();
        SensorEntity sensorEntity = mSensorFacade.getSensorEntity(session, mLightSensor);
        assertNotNull(sensorEntity);
        // Test multiple sensor insertion inside the database.
        for (int i = 0; i < 3; i++) {
            mSensorFacade.getSensorEntity(session, mLightSensor);
        }
        assertEquals(sensorEntity, mSensorFacade.getSensorEntity(session, mLightSensor));
        assertEquals(1, mSensorFacade.getAllSensorEntities(session).size());
    }

    /**
     * Test the insertion of one multiple {@link SensorEntity} inside the database.
     *
     * @see SensorEntityFacade#getSensorEntity(DaoSession, Sensor)
     */
    @MediumTest
    public void testDifferentSensorsOneSingleInsertion() {
        testPreConditions();
        //Insert a light sensor inside the database.
        final DaoSession session = mDatabaseConnector.getSession();
        final SensorEntity lightSensorEntity = mSensorFacade.getSensorEntity(session, mLightSensor);
        assertNotNull(lightSensorEntity);
        //Insert a proximity sensor inside the database.
        final SensorEntity proximitySensorEntity = mSensorFacade.getSensorEntity(session, mProximitySensor);
        assertNotNull(proximitySensorEntity);
        //Checks if two sensors are inside the database.
        assertEquals(2, mSensorFacade.getAllSensorEntities(session).size());
        assertTrue(mSensorFacade.getAllSensorEntities(session).contains(lightSensorEntity));
        assertTrue(mSensorFacade.getAllSensorEntities(session).contains(proximitySensorEntity));
    }

    /**
     * Test the insertion of one multiple {@link SensorEntity} inside the database
     * while this elements are not ordered.
     *
     * @see SensorEntityFacade#getSensorEntity(DaoSession, Sensor)
     */
    @MediumTest
    public void testDisorderedMultipleInsertion() {
        testPreConditions();
        //Insert a single sensor of each type inside the database.
        final DaoSession session = mDatabaseConnector.getSession();
        final SensorEntity lightSensorEntity = mSensorFacade.getSensorEntity(session, mLightSensor);
        final SensorEntity proximitySensorEntity = mSensorFacade.getSensorEntity(session, mProximitySensor);
        assertNotNull(lightSensorEntity);
        assertNotNull(proximitySensorEntity);
        // Test multiple sensor insertion inside the database.
        for (int i = 0; i < 3; i++) {
            mSensorFacade.getSensorEntity(session, mProximitySensor);
            mSensorFacade.getSensorEntity(session, mLightSensor);
        }
        // Test if the insertion has not produced duplicated sensor entities.
        assertEquals(2, mSensorFacade.getAllSensorEntities(session).size());
        assertTrue(mSensorFacade.getAllSensorEntities(session).contains(lightSensorEntity));
        assertTrue(mSensorFacade.getAllSensorEntities(session).contains(proximitySensorEntity));
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