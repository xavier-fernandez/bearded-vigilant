package com.bearded.modules.sensor.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.bearded.modules.sensor.persistence.dao.SensorMeasurementEntityDao;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import static com.bearded.common.time.TimeUtils.timestampToISOString;
import static com.bearded.modules.sensor.persistence.dao.SensorMeasurementEntityDao.Properties.BinSize;
import static com.bearded.modules.sensor.persistence.dao.SensorMeasurementEntityDao.Properties.EndTimestamp;
import static com.bearded.modules.sensor.persistence.dao.SensorMeasurementEntityDao.Properties.MedianSensorValue;
import static com.bearded.modules.sensor.persistence.dao.SensorMeasurementEntityDao.Properties.StartTimestamp;

public class SensorMeasurementEntityTest extends AbstractDaoTestLongPk<SensorMeasurementEntityDao, SensorMeasurementEntity> {

    private static final float TEST_SENSOR_VALUE = 15f;
    @NonNull
    private static final DateTime TEST_END_TIME = DateTime.now();
    @NonNull
    private static final String TEST_END_TIMESTAMP = timestampToISOString(TEST_END_TIME);
    @NonNull
    private static final String TEST_START_TIMESTAMP = timestampToISOString(TEST_END_TIME.minusMinutes(1));

    public SensorMeasurementEntityTest() {
        super(SensorMeasurementEntityDao.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    protected SensorMeasurementEntity createEntity(@Nullable final Long key) {
        final SensorMeasurementSeriesEntity seriesEntity =
                new SensorMeasurementSeriesEntityTest().createEntity(1l);
        final SensorMeasurementEntity entity = new SensorMeasurementEntity();
        entity.setId(key);
        entity.setSensorMeasurementSeriesEntity(seriesEntity);
        entity.setMedianSensorValue(TEST_SENSOR_VALUE);
        entity.setStartTimestamp(TEST_START_TIMESTAMP);
        entity.setEndTimestamp(TEST_END_TIMESTAMP);
        entity.setBinSize((short) 1);
        return entity;
    }

    /**
     * Test if the entity attributes have been inserted properly.
     */
    @MediumTest
    public void testEntityAttributes() {
        final SensorMeasurementEntity entity = this.createEntity(1l);
        assertEquals(entity.getEndTimestamp(), TEST_END_TIMESTAMP);
        assertEquals(entity.getStartTimestamp(), TEST_START_TIMESTAMP);
        assertEquals(entity.getMedianSensorValue(), TEST_SENSOR_VALUE);
        assertEquals(entity.getBinSize(), 1);
    }

    /**
     * Test the @see SensorEntity#toJson
     */
    @SmallTest
    public void testToJson() {
        final SensorMeasurementEntity entity = this.createEntity(1l);
        final JsonObject jsonObject = entity.toJsonObject();
        assertEquals(jsonObject.get(StartTimestamp.name).getAsString(), TEST_START_TIMESTAMP);
        assertEquals(jsonObject.get(EndTimestamp.name).getAsString(), TEST_END_TIMESTAMP);
        assertEquals(jsonObject.get(MedianSensorValue.name).getAsFloat(), TEST_SENSOR_VALUE);
        assertEquals(jsonObject.get(BinSize.name).getAsInt(), 1);
    }
}