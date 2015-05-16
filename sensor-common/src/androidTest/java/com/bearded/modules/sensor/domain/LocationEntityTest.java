package com.bearded.modules.sensor.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.bearded.common.time.TimeUtils;
import com.bearded.modules.sensor.persistence.dao.LocationEntityDao;
import com.google.gson.JsonObject;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import static com.bearded.modules.sensor.persistence.dao.LocationEntityDao.Properties.AccuracyInMeters;
import static com.bearded.modules.sensor.persistence.dao.LocationEntityDao.Properties.Latitude;
import static com.bearded.modules.sensor.persistence.dao.LocationEntityDao.Properties.Longitude;
import static com.bearded.modules.sensor.persistence.dao.LocationEntityDao.Properties.SpeedInMetersSecond;
import static com.bearded.modules.sensor.persistence.dao.LocationEntityDao.Properties.Timestamp;

public class LocationEntityTest extends AbstractDaoTestLongPk<LocationEntityDao, LocationEntity> {

    private static final float TEST_LATITUDE = 35.5f;
    private static final float TEST_LONGITUDE = 30f;
    @NonNull
    private static final String TEST_TIMESTAMP = TimeUtils.nowToISOString();

    public LocationEntityTest() {
        super(LocationEntityDao.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    protected LocationEntity createEntity(@Nullable final Long key) {
        final LocationEntity entity = new LocationEntity();
        entity.setId(key);
        entity.setLatitude(TEST_LATITUDE);
        entity.setLongitude(TEST_LONGITUDE);
        entity.setTimestamp(TEST_TIMESTAMP);
        return entity;
    }

    /**
     * Test if the entity attributes have been inserted properly.
     */
    @MediumTest
    public void testEntityAttributes() {
        final LocationEntity entity = this.createEntity(1l);
        assertTrue(entity.getLatitude() == TEST_LATITUDE);
        assertTrue(entity.getLongitude() == TEST_LONGITUDE);
        assertEquals(entity.getTimestamp(), TEST_TIMESTAMP);
        assertNull(entity.getAccuracyInMeters());
        assertNull(entity.getSpeedInMetersSecond());
    }

    /**
     * Test the @see LocationEntity#toJson
     */
    @SmallTest
    public void testToJson() {
        final LocationEntity entity = this.createEntity(1l);
        final JsonObject jsonObject = entity.toJsonObject();
        assertEquals(jsonObject.get(Latitude.name).getAsFloat(), TEST_LATITUDE);
        assertEquals(jsonObject.get(Longitude.name).getAsFloat(), TEST_LONGITUDE);
        assertEquals(jsonObject.get(Timestamp.name).getAsString(), TEST_TIMESTAMP);
        assertNotNull(jsonObject.get(AccuracyInMeters.name));
        assertNotNull(jsonObject.get(SpeedInMetersSecond.name));
    }
}
