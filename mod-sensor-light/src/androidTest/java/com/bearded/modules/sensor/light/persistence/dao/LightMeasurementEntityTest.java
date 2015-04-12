package com.bearded.modules.sensor.light.persistence.dao;

import com.bearded.common.utils.TimeUtils;
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
        entity.setMidLightValue(5f);
        entity.setStartTimestamp(TimeUtils.timestampToISOString(System.currentTimeMillis()));
        entity.setEndTimestamp(TimeUtils.timestampToISOString(System.currentTimeMillis() + 10000));
        entity.setBinSize((short) 5);
        return entity;
    }

}
