package com.bearded.modules.ble.discovery.persistence.dao;

import com.bearded.modules.ble.discovery.domain.BleEventEntity;

import java.util.Date;
import java.util.Random;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class BleEventEntityTest extends AbstractDaoTestLongPk<BleEventEntityDao, BleEventEntity> {

    private static short ONE_SECOND_IN_MILLISECONDS = 1000;
    private static int EVENT_SERIES_TEST_DURATION_SECONDS = 10 * ONE_SECOND_IN_MILLISECONDS;

    public BleEventEntityTest() {
        super(BleEventEntityDao.class);
    }

    private static Date getStartTimestamp() {
        return new Date(System.currentTimeMillis() - EVENT_SERIES_TEST_DURATION_SECONDS);
    }

    @Override
    protected BleEventEntity createEntity(Long key) {
        BleEventEntity entity = new BleEventEntity();
        entity.setId(key);
        entity.setStartTimestamp(getStartTimestamp());
        entity.setReceivedSignalStrength((byte) (0 - new Random().nextInt(101)));
        return entity;
    }
}