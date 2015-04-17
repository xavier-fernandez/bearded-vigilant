package com.bearded.modules.sensor.internal.persistence.dao;

import com.bearded.modules.sensor.internal.domain.InternalSensorMeasurementEntity;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class InternalSensorMeasurementEntityTest extends AbstractDaoTestLongPk<InternalSensorMeasurementEntityDao, InternalSensorMeasurementEntity> {

    public InternalSensorMeasurementEntityTest() {
        super(InternalSensorMeasurementEntityDao.class);
    }

    @Override
    protected InternalSensorMeasurementEntity createEntity(Long key) {
        InternalSensorMeasurementEntity entity = new InternalSensorMeasurementEntity();
        entity.setId(key);
        entity.setMeasurement_series_id(1);
        entity.setSensorValue(15f);
        entity.setStartTimestamp("GWRrgq");
        entity.setEndTimestamp("gprehwer");
        entity.setBinSize((short) 1);
        return entity;
    }

}
