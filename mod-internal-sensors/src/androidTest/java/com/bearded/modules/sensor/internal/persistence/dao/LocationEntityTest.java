package com.bearded.modules.sensor.internal.persistence.dao;

import com.bearded.modules.sensor.internal.domain.LocationEntity;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class LocationEntityTest extends AbstractDaoTestLongPk<LocationEntityDao, LocationEntity> {

    public LocationEntityTest() {
        super(LocationEntityDao.class);
    }

    @Override
    protected LocationEntity createEntity(Long key) {
        LocationEntity entity = new LocationEntity();
        entity.setId(key);
        entity.setLatitude(50f);
        entity.setLongitude(50f);
        return entity;
    }

}
