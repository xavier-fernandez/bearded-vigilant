package com.bearded.modules.sensor.persistence.dao;

import android.database.sqlite.SQLiteDatabase;

import com.bearded.modules.sensor.domain.LocationEntity;
import com.bearded.modules.sensor.domain.SensorEntity;
import com.bearded.modules.sensor.domain.SensorMeasurementEntity;
import com.bearded.modules.sensor.domain.SensorMeasurementSeriesEntity;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 *
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig sensorEntityDaoConfig;
    private final DaoConfig sensorMeasurementSeriesEntityDaoConfig;
    private final DaoConfig locationEntityDaoConfig;
    private final DaoConfig sensorMeasurementEntityDaoConfig;

    private final SensorEntityDao sensorEntityDao;
    private final SensorMeasurementSeriesEntityDao sensorMeasurementSeriesEntityDao;
    private final LocationEntityDao locationEntityDao;
    private final SensorMeasurementEntityDao sensorMeasurementEntityDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        sensorEntityDaoConfig = daoConfigMap.get(SensorEntityDao.class).clone();
        sensorEntityDaoConfig.initIdentityScope(type);

        sensorMeasurementSeriesEntityDaoConfig = daoConfigMap.get(SensorMeasurementSeriesEntityDao.class).clone();
        sensorMeasurementSeriesEntityDaoConfig.initIdentityScope(type);

        locationEntityDaoConfig = daoConfigMap.get(LocationEntityDao.class).clone();
        locationEntityDaoConfig.initIdentityScope(type);

        sensorMeasurementEntityDaoConfig = daoConfigMap.get(SensorMeasurementEntityDao.class).clone();
        sensorMeasurementEntityDaoConfig.initIdentityScope(type);

        sensorEntityDao = new SensorEntityDao(sensorEntityDaoConfig, this);
        sensorMeasurementSeriesEntityDao = new SensorMeasurementSeriesEntityDao(sensorMeasurementSeriesEntityDaoConfig, this);
        locationEntityDao = new LocationEntityDao(locationEntityDaoConfig, this);
        sensorMeasurementEntityDao = new SensorMeasurementEntityDao(sensorMeasurementEntityDaoConfig, this);

        registerDao(SensorEntity.class, sensorEntityDao);
        registerDao(SensorMeasurementSeriesEntity.class, sensorMeasurementSeriesEntityDao);
        registerDao(LocationEntity.class, locationEntityDao);
        registerDao(SensorMeasurementEntity.class, sensorMeasurementEntityDao);
    }

    public void clear() {
        sensorEntityDaoConfig.getIdentityScope().clear();
        sensorMeasurementSeriesEntityDaoConfig.getIdentityScope().clear();
        locationEntityDaoConfig.getIdentityScope().clear();
        sensorMeasurementEntityDaoConfig.getIdentityScope().clear();
    }

    public SensorEntityDao getSensorEntityDao() {
        return sensorEntityDao;
    }

    public SensorMeasurementSeriesEntityDao getSensorMeasurementSeriesEntityDao() {
        return sensorMeasurementSeriesEntityDao;
    }

    public LocationEntityDao getLocationEntityDao() {
        return locationEntityDao;
    }

    public SensorMeasurementEntityDao getSensorMeasurementEntityDao() {
        return sensorMeasurementEntityDao;
    }

}