package com.bearded.modules.sensor.internal.domain;

import android.support.annotation.NonNull;
import android.test.suitebuilder.annotation.SmallTest;

import com.bearded.modules.sensor.internal.persistence.dao.InternalSensorMeasurementSeriesEntityDao;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import static com.bearded.common.time.TimeUtils.timestampToISOString;

import static com.bearded.modules.sensor.internal.persistence.dao.InternalSensorMeasurementSeriesEntityDao.Properties.EndTimestamp;
import static com.bearded.modules.sensor.internal.persistence.dao.InternalSensorMeasurementSeriesEntityDao.Properties.StartTimestamp;

public class InternalSensorMeasurementSeriesEntityTest extends AbstractDaoTestLongPk<InternalSensorMeasurementSeriesEntityDao, InternalSensorMeasurementSeriesEntity> {

    @NonNull
    private static final DateTime END_TIME = DateTime.now();
    @NonNull
    private static final String END_TIMESTAMP = timestampToISOString(END_TIME);
    @NonNull
    private static final String START_TIMESTAMP = timestampToISOString(END_TIME.minusMinutes(1));

    public InternalSensorMeasurementSeriesEntityTest() {
        super(InternalSensorMeasurementSeriesEntityDao.class);
    }

    /**
     * {@inheritDoc}
     */
    @SmallTest
    @Override
    protected InternalSensorMeasurementSeriesEntity createEntity(Long key) {
        final InternalSensorEntity sensorEntity = new InternalSensorEntityTest().createEntity(1l);
        final InternalSensorMeasurementSeriesEntity entity = new InternalSensorMeasurementSeriesEntity();
        entity.setId(key);
        entity.setInternalSensorEntity(sensorEntity);
        entity.setStartTimestamp(START_TIMESTAMP);
        entity.setEndTimestamp(END_TIMESTAMP);
        return entity;
    }

    /**
     * Test if the created sensor entities have all the inserted attributes.
     */
    @SmallTest
    public void testEntityAttributes(){
        final InternalSensorMeasurementSeriesEntity seriesEntity = createEntity(1l);
        assertEquals(seriesEntity.getStartTimestamp(), START_TIMESTAMP);
        assertEquals(seriesEntity.getEndTimestamp(), END_TIMESTAMP);
        assertNotNull(seriesEntity.getInternalSensorEntity());
    }


    /**
     * Test the @see InternalSensorMeasurementSeriesEntity#toJson
     */
    @SmallTest
    public void testToJson() {
        final InternalSensorMeasurementSeriesEntity seriesEntity = createEntity(1l);
        final JsonObject jsonObject = seriesEntity.toJsonObject();
        assertEquals(jsonObject.get(StartTimestamp.name).getAsString(), START_TIMESTAMP);
        assertEquals(jsonObject.get(EndTimestamp.name).getAsString(), END_TIMESTAMP);
    }
}