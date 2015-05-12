package com.bearded.modules.ble.discovery.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.bearded.modules.ble.discovery.persistence.dao.BleEventSeriesEntityDao;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import static com.bearded.common.time.TimeUtils.timestampToISOString;
import static com.bearded.modules.ble.discovery.persistence.dao.BleEventSeriesEntityDao.Properties.EndTimestamp;
import static com.bearded.modules.ble.discovery.persistence.dao.BleEventSeriesEntityDao.Properties.StartTimestamp;

public class BleEventSeriesEntityTest extends AbstractDaoTestLongPk<BleEventSeriesEntityDao, BleEventSeriesEntity> {

    @NonNull
    private static final DateTime TEST_END_TIME = DateTime.now();
    @NonNull
    private static final String TEST_END_TIMESTAMP = timestampToISOString(TEST_END_TIME);
    @NonNull
    private static final String TEST_START_TIMESTAMP = timestampToISOString(TEST_END_TIME.minusMinutes(1));

    public BleEventSeriesEntityTest() {
        super(BleEventSeriesEntityDao.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    protected BleEventSeriesEntity createEntity(@Nullable final Long key) {
        final BleEventSeriesEntity entity = new BleEventSeriesEntity();
        entity.setId(key);
        entity.setEndTimestamp(TEST_END_TIMESTAMP);
        entity.setStartTimestamp(TEST_START_TIMESTAMP);
        return entity;
    }

    /**
     * Test if the entity attributes have been set properly.
     */
    @MediumTest
    public void testEntityAttributes() {
        final BleEventSeriesEntity eventEntity = this.createEntity(1l);
        assertEquals(eventEntity.getStartTimestamp(), TEST_START_TIMESTAMP);
        assertEquals(eventEntity.getEndTimestamp(), TEST_END_TIMESTAMP);
    }
    
    /**
     * Test the @see BleEventSeriesEntity#toJson
     */
    @SmallTest
    public void testToJson() {
        final BleEventSeriesEntity entity = this.createEntity(1l);
        final JsonObject jsonObject = entity.toJsonObject();
        assertEquals(jsonObject.get(StartTimestamp.name).getAsString(), TEST_START_TIMESTAMP);
        assertEquals(jsonObject.get(EndTimestamp.name).getAsString(), TEST_END_TIMESTAMP);
    }
}