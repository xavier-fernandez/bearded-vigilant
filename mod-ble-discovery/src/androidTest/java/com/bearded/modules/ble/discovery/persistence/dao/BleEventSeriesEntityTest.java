package com.bearded.modules.ble.discovery.persistence.dao;

import com.bearded.modules.ble.discovery.domain.BleEventSeriesEntity;

import java.util.Date;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class BleEventSeriesEntityTest extends AbstractDaoTestLongPk<BleEventSeriesEntityDao, BleEventSeriesEntity> {

    private static short ONE_SECOND_IN_MILLISECONDS = 1000;
    private static int EVENT_SERIES_TEST_DURATION_SECONDS = 10 * ONE_SECOND_IN_MILLISECONDS;

    public BleEventSeriesEntityTest() {
        super(BleEventSeriesEntityDao.class);
    }

    private static Date getStartTimestamp() {
        return new Date(System.currentTimeMillis() - EVENT_SERIES_TEST_DURATION_SECONDS);
    }

    @Override
    protected BleEventSeriesEntity createEntity(Long key) {
        BleEventSeriesEntity entity = new BleEventSeriesEntity();
        entity.setId(key);
        entity.setStartTimestamp(getStartTimestamp());
        return entity;
    }
}