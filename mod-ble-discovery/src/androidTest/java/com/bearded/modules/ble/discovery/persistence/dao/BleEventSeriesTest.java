package com.bearded.modules.ble.discovery.persistence.dao;

import com.bearded.modules.ble.discovery.domain.BleEventSeries;

import java.util.Date;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class BleEventSeriesTest extends AbstractDaoTestLongPk<BleEventSeriesDao, BleEventSeries> {

    public BleEventSeriesTest() {
        super(BleEventSeriesDao.class);
    }

    private static short ONE_SECOND_IN_MILLISECONDS = 1000;
    private static int EVENT_SERIES_TEST_DURATION_SECONDS = 10 * ONE_SECOND_IN_MILLISECONDS;

    @Override
    protected BleEventSeries createEntity(Long key) {
        BleEventSeries entity = new BleEventSeries();
        entity.setId(key);
        entity.setStartTimestamp(getStartTimestamp());
        return entity;
    }

    private static Date getStartTimestamp(){
        return new Date (System.currentTimeMillis() - EVENT_SERIES_TEST_DURATION_SECONDS);
    }

}
