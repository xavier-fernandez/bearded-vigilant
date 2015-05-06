package com.bearded.modules.sensor.internal.domain;

import com.bearded.modules.sensor.internal.domain.InternalSensorMeasurementSeriesEntity;
import com.bearded.modules.sensor.internal.persistence.dao.InternalSensorMeasurementSeriesEntityDao;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class InternalSensorMeasurementSeriesEntityTest extends AbstractDaoTestLongPk<InternalSensorMeasurementSeriesEntityDao, InternalSensorMeasurementSeriesEntity> {

    public InternalSensorMeasurementSeriesEntityTest() {
        super(InternalSensorMeasurementSeriesEntityDao.class);
    }

    @Override
    protected InternalSensorMeasurementSeriesEntity createEntity(Long key) {
        InternalSensorMeasurementSeriesEntity entity = new InternalSensorMeasurementSeriesEntity();
        entity.setId(key);
        entity.setSensor_id(521);
        entity.setStartTimestamp("ogjeo");
        return entity;
    }

}
