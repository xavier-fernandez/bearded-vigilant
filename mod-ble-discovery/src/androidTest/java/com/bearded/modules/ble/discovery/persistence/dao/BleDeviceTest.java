package com.bearded.modules.ble.discovery.persistence.dao;

import com.bearded.modules.ble.discovery.domain.BleDevice;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class BleDeviceTest extends AbstractDaoTestLongPk<BleDeviceDao, BleDevice> {

    public BleDeviceTest() {
        super(BleDeviceDao.class);
    }

    @Override
    protected BleDevice createEntity(Long key) {
        BleDevice entity = new BleDevice();
        entity.setId(key);
        entity.setDeviceAddress();
        return entity;
    }

}
