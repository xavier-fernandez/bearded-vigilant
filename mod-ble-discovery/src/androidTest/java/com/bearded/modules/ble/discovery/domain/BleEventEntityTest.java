package com.bearded.modules.ble.discovery.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.bearded.modules.ble.discovery.persistence.dao.BleEventEntityDao;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;

import java.util.Random;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import static com.bearded.common.time.TimeUtils.timestampToISOString;
import static com.bearded.modules.ble.discovery.persistence.dao.BleEventEntityDao.Properties.EndTimestamp;
import static com.bearded.modules.ble.discovery.persistence.dao.BleEventEntityDao.Properties.ReceivedSignalStrength;
import static com.bearded.modules.ble.discovery.persistence.dao.BleEventEntityDao.Properties.StartTimestamp;

public class BleEventEntityTest extends AbstractDaoTestLongPk<BleEventEntityDao, BleEventEntity> {

    @NonNull
    private static final DateTime TEST_END_TIME = DateTime.now();
    @NonNull
    private static final String TEST_END_TIMESTAMP = timestampToISOString(TEST_END_TIME);
    @NonNull
    private static final String TEST_START_TIMESTAMP = timestampToISOString(TEST_END_TIME.minusMinutes(1));
    private static final byte TEST_RSSI = (byte) (0 - new Random().nextInt(101));

    public BleEventEntityTest() {
        super(BleEventEntityDao.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    protected BleEventEntity createEntity(@Nullable final Long key) {
        final BleEventEntity entity = new BleEventEntity();
        entity.setId(key);
        entity.setStartTimestamp(TEST_START_TIMESTAMP);
        entity.setEndTimestamp(TEST_END_TIMESTAMP);
        entity.setReceivedSignalStrength(TEST_RSSI);
        return entity;
    }

    /**
     * Test if the created sensor entities have all the inserted attributes.
     */
    @MediumTest
    public void testEntityAttributes() {
        final BleEventEntity eventEntity = createEntity(1l);
        assertEquals(eventEntity.getStartTimestamp(), TEST_START_TIMESTAMP);
        assertEquals(eventEntity.getEndTimestamp(), TEST_END_TIMESTAMP);
        assertEquals(eventEntity.getReceivedSignalStrength(), TEST_RSSI);
    }

    /**
     * Test the @see BleEventEntity#toJson
     */
    @SmallTest
    public void testToJson() {
        final BleEventEntity entity = this.createEntity(1l);
        final JsonObject jsonObject = entity.toJsonObject();
        assertEquals(jsonObject.get(StartTimestamp.name).getAsString(), TEST_START_TIMESTAMP);
        assertEquals(jsonObject.get(EndTimestamp.name).getAsString(), TEST_END_TIMESTAMP);
        assertEquals(jsonObject.get(ReceivedSignalStrength.name).getAsByte(), TEST_RSSI);
    }
}