package com.bearded.modules.ble.discovery.persistence.dao;

import com.bearded.modules.ble.discovery.domain.BleEventSeriesEntity;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import static com.bearded.common.utils.TimeUtils.timestampToISOString;

public class BleEventSeriesEntityTest extends AbstractDaoTestLongPk<BleEventSeriesEntityDao, BleEventSeriesEntity> {

    private static final short ONE_SECOND_IN_MILLISECONDS = 1000;
    private static final int EVENT_SERIES_TEST_DURATION_SECONDS = 10 * ONE_SECOND_IN_MILLISECONDS;

    public BleEventSeriesEntityTest() {
        super(BleEventSeriesEntityDao.class);
    }

    /**
     * Returns a device entity using a start timestamp from ten seconds before this method calling.
     *
     * @return {@link java.lang.String} with the start timestamp following the ISO 8601 convention.
     */
    private static String getIsoStartTimestamp() {
        return timestampToISOString(System.currentTimeMillis() - EVENT_SERIES_TEST_DURATION_SECONDS);
    }

    @Override
    protected BleEventSeriesEntity createEntity(Long key) {
        final BleEventSeriesEntity entity = new BleEventSeriesEntity();
        entity.setId(key);
        entity.setStartTimestamp(getIsoStartTimestamp());
        return entity;
    }
}