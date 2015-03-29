package com.bearded.modules.ble.discovery.persistence.dao;

import com.bearded.modules.ble.discovery.domain.BleEventSeries;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class BleEventSeriesTest extends AbstractDaoTestLongPk<BleEventSeriesDao, BleEventSeries> {

    public BleEventSeriesTest() {
        super(BleEventSeriesDao.class);
    }

    @Override
    protected BleEventSeries createEntity(Long key) {
        BleEventSeries entity = new BleEventSeries();
        entity.setId(key);
        entity.setStartTimestamp();
        return entity;
    }

}
