package com.bearded.modules.ble.discovery.domain;

import android.support.annotation.NonNull;

import com.bearded.modules.ble.discovery.persistence.dao.BleEventEntityDao;

import java.util.Random;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import static com.bearded.common.time.TimeUtils.timestampToISOString;

public class BleEventEntityTest extends AbstractDaoTestLongPk<BleEventEntityDao, BleEventEntity> {

    private static final short ONE_SECOND_IN_MILLISECONDS = 1000;
    private static final int EVENT_SERIES_TEST_DURATION_SECONDS = 10 * ONE_SECOND_IN_MILLISECONDS;

    public BleEventEntityTest() {
        super(BleEventEntityDao.class);
    }

    /**
     * Returns a device entity using a start timestamp from ten seconds before this method calling.
     *
     * @return {@link java.lang.String} with the start timestamp following the ISO 8601 convention.
     */
    @NonNull
    private static String getIsoStartTimestamp() {
        return timestampToISOString(System.currentTimeMillis() - EVENT_SERIES_TEST_DURATION_SECONDS);
    }

    @Override
    protected BleEventEntity createEntity(Long key) {
        final BleEventEntity entity = new BleEventEntity();
        entity.setId(key);
        entity.setStartTimestamp(getIsoStartTimestamp());
        entity.setReceivedSignalStrength((byte) (0 - new Random().nextInt(101)));
        return entity;
    }
}