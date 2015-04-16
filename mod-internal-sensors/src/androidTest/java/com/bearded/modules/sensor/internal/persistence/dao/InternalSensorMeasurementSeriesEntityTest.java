package com.bearded.modules.sensor.internal.persistence.dao;

import com.bearded.modules.sensor.internal.domain.InternalSensorMeasurementSeriesEntity;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class InternalSensorMeasurementSeriesEntityTest extends AbstractDaoTestLongPk<InternalSensorMeasurementSeriesEntityDao, InternalSensorMeasurementSeriesEntity> {

    public InternalSensorMeasurementSeriesEntityTest() {
        super(InternalSensorMeasurementSeriesEntityDao.class);
    }

    @Override
    protected InternalSensorMeasurementSeriesEntity createEntity(Long key) {
        InternalSensorMeasurementSeriesEntity entity = new InternalSensorMeasurementSeriesEntity();
        entity.setId(key);
        entity.setSensor_id();
        entity.setStartTimestamp();
        return entity;
    }

}
