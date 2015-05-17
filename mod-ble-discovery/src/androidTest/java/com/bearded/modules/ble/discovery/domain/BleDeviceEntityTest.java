package com.bearded.modules.ble.discovery.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.bearded.modules.ble.discovery.persistence.dao.BleDeviceEntityDao;
import com.google.gson.JsonObject;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import static com.bearded.modules.ble.discovery.persistence.dao.BleDeviceEntityDao.Properties.AdvertiseName;
import static com.bearded.modules.ble.discovery.persistence.dao.BleDeviceEntityDao.Properties.DeviceAddress;
import static com.bearded.modules.ble.discovery.persistence.dao.BleDeviceEntityDao.Properties.IsEdrOrBr;
import static com.bearded.modules.ble.discovery.persistence.dao.BleDeviceEntityDao.Properties.IsLowEnergy;

public class BleDeviceEntityTest extends AbstractDaoTestLongPk<BleDeviceEntityDao, BleDeviceEntity> {

    private static final String TEST_DEVICE_ADDRESS = "AA:BB:CC:DD:EE:FF";
    private static final String TEST_ADVERTISE_NAME = "Advertise Name";

    public BleDeviceEntityTest() {
        super(BleDeviceEntityDao.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    protected BleDeviceEntity createEntity(@Nullable final Long key) {
        final BleDeviceEntity entity = new BleDeviceEntity();
        entity.setId(key);
        entity.setDeviceAddress(TEST_DEVICE_ADDRESS);
        entity.setAdvertiseName(TEST_ADVERTISE_NAME);
        return entity;
    }

    /**
     * Test if the entity attributes have been inserted properly.
     */
    @MediumTest
    public void testEntityAttributes() {
        final long deviceId = 1l;
        final BleDeviceEntity entity = this.createEntity(deviceId);
        assertEquals(entity.getDeviceAddress(), TEST_DEVICE_ADDRESS);
        assertEquals(entity.getAdvertiseName(), TEST_ADVERTISE_NAME);
        assertEquals(entity.getId().longValue(), deviceId);
    }

    /**
     * Test the @see BleDeviceEntity#toJson
     */
    @SmallTest
    public void testToJson() {
        final BleDeviceEntity entity = this.createEntity(1l);
        final JsonObject jsonObject = entity.toJsonObject();
        assertEquals(jsonObject.get(DeviceAddress.name).getAsString(), TEST_DEVICE_ADDRESS);
        assertEquals(jsonObject.get(AdvertiseName.name).getAsString(), TEST_ADVERTISE_NAME);
        assertNull(jsonObject.get(IsEdrOrBr.name));
        assertNull(jsonObject.get(IsLowEnergy.name));
    }
}