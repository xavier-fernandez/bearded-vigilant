package com.bearded.modules.ble.discovery.persistence.dao;

import com.bearded.modules.ble.discovery.domain.BleEvent;

import java.util.Date;
import java.util.Random;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class BleEventTest extends AbstractDaoTestLongPk<BleEventDao, BleEvent> {

    private static short ONE_SECOND_IN_MILLISECONDS = 1000;
    private static int EVENT_SERIES_TEST_DURATION_SECONDS = 10 * ONE_SECOND_IN_MILLISECONDS;

    public BleEventTest() {
        super(BleEventDao.class);
    }

    @Override
    protected BleEvent createEntity(Long key) {
        BleEvent entity = new BleEvent();
        entity.setId(key);
        entity.setStartTimestamp(getStartTimestamp());
        entity.setReceivedSignalStrength((byte) (0 - new Random().nextInt(101)));
        return entity;
    }


    private static Date getStartTimestamp(){
        return new Date (System.currentTimeMillis() - EVENT_SERIES_TEST_DURATION_SECONDS);
    }
}