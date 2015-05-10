package com.bearded.modules.sensor.internal.domain;

import android.support.annotation.NonNull;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.bearded.modules.sensor.internal.persistence.dao.InternalSensorEntityDao;
import com.google.gson.JsonObject;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import static com.bearded.modules.sensor.internal.persistence.dao.InternalSensorEntityDao.Properties.FifoMaxEventCount;
import static com.bearded.modules.sensor.internal.persistence.dao.InternalSensorEntityDao.Properties.FifoReservedEventCount;
import static com.bearded.modules.sensor.internal.persistence.dao.InternalSensorEntityDao.Properties.MaximumDelayMicroseconds;
import static com.bearded.modules.sensor.internal.persistence.dao.InternalSensorEntityDao.Properties.MaximumRange;
import static com.bearded.modules.sensor.internal.persistence.dao.InternalSensorEntityDao.Properties.MinimumDelayMicroseconds;
import static com.bearded.modules.sensor.internal.persistence.dao.InternalSensorEntityDao.Properties.PowerInMilliAmperes;
import static com.bearded.modules.sensor.internal.persistence.dao.InternalSensorEntityDao.Properties.ReportingMode;
import static com.bearded.modules.sensor.internal.persistence.dao.InternalSensorEntityDao.Properties.SensorName;
import static com.bearded.modules.sensor.internal.persistence.dao.InternalSensorEntityDao.Properties.SensorResolution;
import static com.bearded.modules.sensor.internal.persistence.dao.InternalSensorEntityDao.Properties.SensorType;
import static com.bearded.modules.sensor.internal.persistence.dao.InternalSensorEntityDao.Properties.SensorUnit;
import static com.bearded.modules.sensor.internal.persistence.dao.InternalSensorEntityDao.Properties.SensorVendor;
import static com.bearded.modules.sensor.internal.persistence.dao.InternalSensorEntityDao.Properties.SensorVersion;

public class InternalSensorEntityTest extends AbstractDaoTestLongPk<InternalSensorEntityDao, InternalSensorEntity> {

    @NonNull
    private static final String TEST_SENSOR_NAME = "TEST_NAME";
    @NonNull
    private static final String TEST_SENSOR_TYPE = "TEST_SENSOR_TYPE";
    @NonNull
    private static final String TEST_SENSOR_UNIT = "TEST_SENSOR_UNIT";

    public InternalSensorEntityTest() {
        super(InternalSensorEntityDao.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @MediumTest
    protected InternalSensorEntity createEntity(@NonNull final Long key) {
        final InternalSensorEntity entity = new InternalSensorEntity(key);
        entity.setId(key);
        entity.setSensorName(TEST_SENSOR_NAME);
        entity.setSensorType(TEST_SENSOR_TYPE);
        entity.setSensorUnit(TEST_SENSOR_UNIT);
        return entity;
    }

    /**
     * Test if the entity attributes have been inserted properly.
     */
    @MediumTest
    public void testEntityAttributes() {
        final InternalSensorEntity entity = this.createEntity(1l);
        assertEquals(entity.getSensorName(), TEST_SENSOR_NAME);
        assertEquals(entity.getSensorType(), TEST_SENSOR_TYPE);
        assertEquals(entity.getSensorUnit(), TEST_SENSOR_UNIT);
    }

    /**
     * Test the @see InternalSensorEntity#toJson
     */
    @SmallTest
    public void testToJson() {
        final InternalSensorEntity entity = this.createEntity(1l);
        final JsonObject jsonObject = entity.toJsonObject();
        assertEquals(jsonObject.get(SensorName.name).getAsString(), TEST_SENSOR_NAME);
        assertEquals(jsonObject.get(SensorType.name).getAsString(), TEST_SENSOR_TYPE);
        assertEquals(jsonObject.get(SensorUnit.name).getAsString(), TEST_SENSOR_UNIT);
        assertNotNull(jsonObject.get(MinimumDelayMicroseconds.name));
        assertNotNull(jsonObject.get(MaximumDelayMicroseconds.name));
        assertNotNull(jsonObject.get(FifoMaxEventCount.name));
        assertNotNull(jsonObject.get(FifoReservedEventCount.name));
        assertNotNull(jsonObject.get(MaximumRange.name));
        assertNotNull(jsonObject.get(ReportingMode.name));
        assertNotNull(jsonObject.get(PowerInMilliAmperes.name));
        assertNotNull(jsonObject.get(SensorResolution.name));
        assertNotNull(jsonObject.get(SensorVendor.name));
        assertNotNull(jsonObject.get(SensorVersion.name));
    }
}