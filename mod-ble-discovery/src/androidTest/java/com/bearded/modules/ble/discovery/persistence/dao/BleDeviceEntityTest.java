package com.bearded.modules.ble.discovery.persistence.dao;

import com.bearded.modules.ble.discovery.domain.BleDeviceEntity;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class BleDeviceEntityTest extends AbstractDaoTestLongPk<BleDeviceEntityDao, BleDeviceEntity> {

    private static final String TEST_DEVICE_ADDRESS = "AA:BB:CC:DD:EE:FF";

    public BleDeviceEntityTest() {
        super(BleDeviceEntityDao.class);
    }

    @Override
    protected BleDeviceEntity createEntity(Long key) {
        final BleDeviceEntity entity = new BleDeviceEntity();
        entity.setId(key);
        entity.setDeviceAddress(TEST_DEVICE_ADDRESS);
        return entity;
    }

}
