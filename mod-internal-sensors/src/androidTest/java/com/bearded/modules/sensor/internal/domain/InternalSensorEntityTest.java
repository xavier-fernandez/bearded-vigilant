package com.bearded.modules.sensor.internal.domain;

import com.bearded.modules.sensor.internal.persistence.dao.InternalSensorEntityDao;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class InternalSensorEntityTest extends AbstractDaoTestLongPk<InternalSensorEntityDao, InternalSensorEntity> {

    public InternalSensorEntityTest() {
        super(InternalSensorEntityDao.class);
    }

    @Override
    protected InternalSensorEntity createEntity(Long key) {
        InternalSensorEntity entity = new InternalSensorEntity();
        entity.setId(key);
        entity.setSensorName("TEST");
        entity.setSensorType("FAKE");
        entity.setSensorUnit("FAKE UNIT");
        return entity;
    }

}
