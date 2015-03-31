package com.bearded.modules.ble.discovery.persistence.dao;

import com.bearded.modules.ble.discovery.domain.BleDevice;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class BleDeviceTest extends AbstractDaoTestLongPk<BleDeviceDao, BleDevice> {

    private static final String TEST_DEVICE_ADDRESS = "AA:BB:CC:DD:EE:FF";

    public BleDeviceTest() {
        super(BleDeviceDao.class);
    }

    @Override
    protected BleDevice createEntity(Long key) {
        BleDevice entity = new BleDevice();
        entity.setId(key);
        entity.setDeviceAddress(TEST_DEVICE_ADDRESS);
        return entity;
    }

}
