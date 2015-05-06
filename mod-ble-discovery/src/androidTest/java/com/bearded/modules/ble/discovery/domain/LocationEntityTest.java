package com.bearded.modules.ble.discovery.domain;

import com.bearded.modules.ble.discovery.persistence.dao.LocationEntityDao;

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
