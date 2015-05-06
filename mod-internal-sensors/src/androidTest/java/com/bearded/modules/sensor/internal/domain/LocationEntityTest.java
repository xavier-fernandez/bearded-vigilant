package com.bearded.modules.sensor.internal.domain;

import com.bearded.modules.sensor.internal.domain.LocationEntity;
import com.bearded.modules.sensor.internal.persistence.dao.LocationEntityDao;

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
