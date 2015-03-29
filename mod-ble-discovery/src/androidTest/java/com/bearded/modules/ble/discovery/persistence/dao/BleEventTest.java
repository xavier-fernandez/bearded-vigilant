package com.bearded.modules.ble.discovery.persistence.dao;

import com.bearded.modules.ble.discovery.domain.BleEvent;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class BleEventTest extends AbstractDaoTestLongPk<BleEventDao, BleEvent> {

    public BleEventTest() {
        super(BleEventDao.class);
    }

    @Override
    protected BleEvent createEntity(Long key) {
        BleEvent entity = new BleEvent();
        entity.setId(key);
        entity.setStartTimestamp();
        entity.setReceivedSignalStrength();
        return entity;
    }

}
