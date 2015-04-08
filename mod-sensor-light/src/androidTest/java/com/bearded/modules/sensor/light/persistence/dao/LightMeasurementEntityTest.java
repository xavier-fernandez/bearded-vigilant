package com.bearded.modules.sensor.light.persistence.dao;

import com.bearded.modules.sensor.light.domain.LightMeasurementEntity;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class LightMeasurementEntityTest extends AbstractDaoTestLongPk<LightMeasurementEntityDao, LightMeasurementEntity> {

    public LightMeasurementEntityTest() {
        super(LightMeasurementEntityDao.class);
    }

    @Override
    protected LightMeasurementEntity createEntity(Long key) {
        LightMeasurementEntity entity = new LightMeasurementEntity();
        entity.setId(key);
        entity.setMidLightValue();
        entity.setStartTimestamp();
        entity.setEndTimestamp();
        entity.setBinSize();
        return entity;
    }

}
