package com.bearded.modules.sensor.internal.domain;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS
// KEEP INCLUDES - put your custom includes here

import android.support.annotation.NonNull;

import com.bearded.modules.sensor.internal.persistence.dao.DaoSession;
import com.bearded.modules.sensor.internal.persistence.dao.InternalSensorMeasurementEntityDao;
import com.bearded.modules.sensor.internal.persistence.dao.InternalSensorMeasurementSeriesEntityDao;
import com.bearded.modules.sensor.internal.persistence.dao.LocationEntityDao;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoException;

import static com.bearded.modules.sensor.internal.persistence.dao.InternalSensorMeasurementEntityDao.Properties.BinSize;
import static com.bearded.modules.sensor.internal.persistence.dao.InternalSensorMeasurementEntityDao.Properties.EndTimestamp;
import static com.bearded.modules.sensor.internal.persistence.dao.InternalSensorMeasurementEntityDao.Properties.Location_id;
import static com.bearded.modules.sensor.internal.persistence.dao.InternalSensorMeasurementEntityDao.Properties.SensorValue;
import static com.bearded.modules.sensor.internal.persistence.dao.InternalSensorMeasurementEntityDao.Properties.StartTimestamp;

// KEEP INCLUDES END

/**
 * Entity mapped to table InternalSensorMeasurement.
 */
public class InternalSensorMeasurementEntity implements com.bearded.common.persistance.ParseableJson, java.lang.Comparable<InternalSensorMeasurementEntity> {

    private Long id;
    private long measurement_series_id;
    private Long location_id;
    private float sensorValue;
    /**
     * Not-null value.
     */
    private String startTimestamp;
    /**
     * Not-null value.
     */
    private String endTimestamp;
    private short binSize;

    /**
     * Used to resolve relations
     */
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    private transient InternalSensorMeasurementEntityDao myDao;

    private InternalSensorMeasurementSeriesEntity internalSensorMeasurementSeriesEntity;
    private Long internalSensorMeasurementSeriesEntity__resolvedKey;

    private LocationEntity locationEntity;
    private Long locationEntity__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public InternalSensorMeasurementEntity() {
    }

    public InternalSensorMeasurementEntity(Long id) {
        this.id = id;
    }

    public InternalSensorMeasurementEntity(Long id, long measurement_series_id, Long location_id, float sensorValue, String startTimestamp, String endTimestamp, short binSize) {
        this.id = id;
        this.measurement_series_id = measurement_series_id;
        this.location_id = location_id;
        this.sensorValue = sensorValue;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.binSize = binSize;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getInternalSensorMeasurementEntityDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getMeasurement_series_id() {
        return measurement_series_id;
    }

    public void setMeasurement_series_id(long measurement_series_id) {
        this.measurement_series_id = measurement_series_id;
    }

    public Long getLocation_id() {
        return location_id;
    }

    public void setLocation_id(Long location_id) {
        this.location_id = location_id;
    }

    public float getSensorValue() {
        return sensorValue;
    }

    public void setSensorValue(float sensorValue) {
        this.sensorValue = sensorValue;
    }

    /**
     * Not-null value.
     */
    public String getStartTimestamp() {
        return startTimestamp;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setStartTimestamp(String startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    /**
     * Not-null value.
     */
    public String getEndTimestamp() {
        return endTimestamp;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setEndTimestamp(String endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public short getBinSize() {
        return binSize;
    }

    public void setBinSize(short binSize) {
        this.binSize = binSize;
    }

    /**
     * To-one relationship, resolved on first access.
     */
    public InternalSensorMeasurementSeriesEntity getInternalSensorMeasurementSeriesEntity() {
        long __key = this.measurement_series_id;
        if (internalSensorMeasurementSeriesEntity__resolvedKey == null || !internalSensorMeasurementSeriesEntity__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            InternalSensorMeasurementSeriesEntityDao targetDao = daoSession.getInternalSensorMeasurementSeriesEntityDao();
            InternalSensorMeasurementSeriesEntity internalSensorMeasurementSeriesEntityNew = targetDao.load(__key);
            synchronized (this) {
                internalSensorMeasurementSeriesEntity = internalSensorMeasurementSeriesEntityNew;
                internalSensorMeasurementSeriesEntity__resolvedKey = __key;
            }
        }
        return internalSensorMeasurementSeriesEntity;
    }

    public void setInternalSensorMeasurementSeriesEntity(InternalSensorMeasurementSeriesEntity internalSensorMeasurementSeriesEntity) {
        if (internalSensorMeasurementSeriesEntity == null) {
            throw new DaoException("To-one property 'measurement_series_id' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.internalSensorMeasurementSeriesEntity = internalSensorMeasurementSeriesEntity;
            measurement_series_id = internalSensorMeasurementSeriesEntity.getId();
            internalSensorMeasurementSeriesEntity__resolvedKey = measurement_series_id;
        }
    }

    /**
     * To-one relationship, resolved on first access.
     */
    public LocationEntity getLocationEntity() {
        Long __key = this.location_id;
        if (locationEntity__resolvedKey == null || !locationEntity__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            LocationEntityDao targetDao = daoSession.getLocationEntityDao();
            LocationEntity locationEntityNew = targetDao.load(__key);
            synchronized (this) {
                locationEntity = locationEntityNew;
                locationEntity__resolvedKey = __key;
            }
        }
        return locationEntity;
    }

    public void setLocationEntity(LocationEntity locationEntity) {
        synchronized (this) {
            this.locationEntity = locationEntity;
            location_id = locationEntity == null ? null : locationEntity.getId();
            locationEntity__resolvedKey = location_id;
        }
    }

    /**
     * Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context.
     */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context.
     */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context.
     */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here

    /**
     * {@inheritDoc}
     * NOTE: This implementation compares the two elements comparing its start timestamp.
     */
    @Override
    public int compareTo(@NonNull final InternalSensorMeasurementEntity another) {
        return this.startTimestamp.compareTo(another.startTimestamp);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public JsonObject toJsonObject() {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.add(Location_id.name, (getLocationEntity() == null)
                ? null : getLocationEntity().toJsonObject());
        jsonObject.add(StartTimestamp.name, new JsonPrimitive(this.startTimestamp));
        jsonObject.add(EndTimestamp.name, new JsonPrimitive(this.endTimestamp));
        jsonObject.add(SensorValue.name, new JsonPrimitive(this.sensorValue));
        jsonObject.add(BinSize.name, new JsonPrimitive(this.binSize));
        return jsonObject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.toJsonObject().toString();
    }
    // KEEP METHODS END

}
