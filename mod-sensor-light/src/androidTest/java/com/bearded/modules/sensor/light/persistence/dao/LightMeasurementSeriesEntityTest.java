package com.bearded.modules.sensor.light.persistence.dao;

import com.bearded.common.utils.TimeUtils;
import com.bearded.modules.sensor.light.domain.LightMeasurementSeriesEntity;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class LightMeasurementSeriesEntityTest extends AbstractDaoTestLongPk<LightMeasurementSeriesEntityDao, LightMeasurementSeriesEntity> {

    public LightMeasurementSeriesEntityTest() {
        super(LightMeasurementSeriesEntityDao.class);
    }

    @Override
    protected LightMeasurementSeriesEntity createEntity(Long key) {
        LightMeasurementSeriesEntity entity = new LightMeasurementSeriesEntity();
        entity.setId(key);
        entity.setStartTimestamp(TimeUtils.timestampToISOString(System.currentTimeMillis()));
        return entity;
    }

}
