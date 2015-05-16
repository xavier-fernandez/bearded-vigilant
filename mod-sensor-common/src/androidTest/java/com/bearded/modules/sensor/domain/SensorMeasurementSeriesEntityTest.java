package com.bearded.modules.sensor.domain;

import android.support.annotation.NonNull;
import android.test.suitebuilder.annotation.SmallTest;

import com.bearded.modules.sensor.persistence.dao.SensorMeasurementSeriesEntityDao;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import static com.bearded.common.time.TimeUtils.timestampToISOString;
import static com.bearded.modules.sensor.persistence.dao.SensorMeasurementSeriesEntityDao.Properties.EndTimestamp;
import static com.bearded.modules.sensor.persistence.dao.SensorMeasurementSeriesEntityDao.Properties.StartTimestamp;

public class SensorMeasurementSeriesEntityTest extends AbstractDaoTestLongPk<SensorMeasurementSeriesEntityDao, SensorMeasurementSeriesEntity> {

    @NonNull
    private static final DateTime END_TIME = DateTime.now();
    @NonNull
    private static final String END_TIMESTAMP = timestampToISOString(END_TIME);
    @NonNull
    private static final String START_TIMESTAMP = timestampToISOString(END_TIME.minusMinutes(1));

    public SensorMeasurementSeriesEntityTest() {
        super(SensorMeasurementSeriesEntityDao.class);
    }

    /**
     * {@inheritDoc}
     */
    @SmallTest
    @Override
    protected SensorMeasurementSeriesEntity createEntity(Long key) {
        final SensorEntity sensorEntity = new SensorEntityTest().createEntity(1l);
        final SensorMeasurementSeriesEntity entity = new SensorMeasurementSeriesEntity();
        entity.setId(key);
        entity.setSensorEntity(sensorEntity);
        entity.setStartTimestamp(START_TIMESTAMP);
        entity.setEndTimestamp(END_TIMESTAMP);
        return entity;
    }

    /**
     * Test if the created sensor entities have all the inserted attributes.
     */
    @SmallTest
    public void testEntityAttributes() {
        final SensorMeasurementSeriesEntity seriesEntity = createEntity(1l);
        assertEquals(seriesEntity.getStartTimestamp(), START_TIMESTAMP);
        assertEquals(seriesEntity.getEndTimestamp(), END_TIMESTAMP);
        assertNotNull(seriesEntity.getSensorEntity());
    }

    /**
     * Test the @see SensorMeasurementSeriesEntity#toJson
     */
    @SmallTest
    public void testToJson() {
        final SensorMeasurementSeriesEntity seriesEntity = createEntity(1l);
        final JsonObject jsonObject = seriesEntity.toJsonObject();
        assertEquals(jsonObject.get(StartTimestamp.name).getAsString(), START_TIMESTAMP);
        assertEquals(jsonObject.get(EndTimestamp.name).getAsString(), END_TIMESTAMP);
    }
}